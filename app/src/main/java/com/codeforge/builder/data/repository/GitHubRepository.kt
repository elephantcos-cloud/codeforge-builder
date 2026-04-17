package com.codeforge.builder.data.repository

import android.content.Context
import com.codeforge.builder.data.local.AppDatabase
import com.codeforge.builder.data.local.entity.BuildRecord
import com.codeforge.builder.data.local.entity.Project
import com.codeforge.builder.data.local.entity.ProjectFile
import com.codeforge.builder.data.preferences.AppPreferences
import com.codeforge.builder.data.remote.NetworkModule
import com.codeforge.builder.data.remote.model.*
import com.codeforge.builder.utils.Constants
import com.codeforge.builder.utils.toBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class GitHubRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val projectDao = db.projectDao()
    private val fileDao = db.projectFileDao()
    private val buildDao = db.buildRecordDao()
    private val api = NetworkModule.apiService
    private val prefs = AppPreferences.getInstance(context)
    private val appContext = context.applicationContext

    // ─── Auth header helper ───────────────────────────────────
    private suspend fun authHeader(): String {
        val token = prefs.githubToken.first()
        return "Bearer $token"
    }

    // ─── User ─────────────────────────────────────────────────
    suspend fun getAuthenticatedUser(): ApiResult<GitHubUser> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAuthenticatedUser(authHeader())
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            ApiResult.Error(message = e.message ?: "Network error")
        }
    }

    // ─── Repositories ─────────────────────────────────────────
    suspend fun createRepo(name: String, private: Boolean = false): ApiResult<GitHubRepo> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.createRepo(
                    authHeader(),
                    CreateRepoRequest(name = name, private = private)
                )
                if (response.isSuccessful) {
                    ApiResult.Success(response.body()!!)
                } else {
                    ApiResult.Error(response.code(), response.message())
                }
            } catch (e: Exception) {
                ApiResult.Error(message = e.message ?: "Network error")
            }
        }

    // ─── Push files to GitHub ──────────────────────────────────
    suspend fun pushFilesToRepo(
        owner: String,
        repoName: String,
        files: List<Pair<String, String>>,  // (path, content)
        commitMessage: String = "Update from CodeForge"
    ): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val auth = authHeader()
            val username = prefs.githubUsername.first()
            val email = prefs.githubEmail.first()
            val committer = CommitAuthor(
                name = username.ifEmpty { "CodeForge" },
                email = email.ifEmpty { "$username@users.noreply.github.com" }
            )

            for ((path, content) in files) {
                // Get existing SHA if file exists
                val existingSha: String? = try {
                    val resp = api.getContents(auth, owner, repoName, path)
                    if (resp.isSuccessful) resp.body()?.sha else null
                } catch (e: Exception) { null }

                val request = CreateFileRequest(
                    message = commitMessage,
                    content = content.toBase64(),
                    sha = existingSha,
                    committer = committer
                )
                val response = api.createOrUpdateFile(auth, owner, repoName, path, request)
                if (!response.isSuccessful && response.code() != 422) {
                    return@withContext ApiResult.Error(response.code(), "Failed to push: $path — ${response.message()}")
                }
            }
            ApiResult.Success(true)
        } catch (e: Exception) {
            ApiResult.Error(message = e.message ?: "Push failed")
        }
    }

    // ─── Workflow Runs ─────────────────────────────────────────
    suspend fun getLatestWorkflowRun(owner: String, repoName: String): ApiResult<WorkflowRun> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getWorkflowRuns(authHeader(), owner, repoName, perPage = 1)
                if (response.isSuccessful) {
                    val runs = response.body()?.workflowRuns
                    if (!runs.isNullOrEmpty()) {
                        ApiResult.Success(runs.first())
                    } else {
                        ApiResult.Error(message = "No workflow runs found")
                    }
                } else {
                    ApiResult.Error(response.code(), response.message())
                }
            } catch (e: Exception) {
                ApiResult.Error(message = e.message ?: "Network error")
            }
        }

    suspend fun getWorkflowRun(owner: String, repoName: String, runId: Long): ApiResult<WorkflowRun> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getWorkflowRun(authHeader(), owner, repoName, runId)
                if (response.isSuccessful) {
                    ApiResult.Success(response.body()!!)
                } else {
                    ApiResult.Error(response.code(), response.message())
                }
            } catch (e: Exception) {
                ApiResult.Error(message = e.message ?: "Network error")
            }
        }

    // ─── Artifacts ─────────────────────────────────────────────
    suspend fun getArtifacts(owner: String, repoName: String, runId: Long): ApiResult<List<Artifact>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getRunArtifacts(authHeader(), owner, repoName, runId)
                if (response.isSuccessful) {
                    ApiResult.Success(response.body()?.artifacts ?: emptyList())
                } else {
                    ApiResult.Error(response.code(), response.message())
                }
            } catch (e: Exception) {
                ApiResult.Error(message = e.message ?: "Network error")
            }
        }

    suspend fun downloadArtifact(
        artifactUrl: String,
        destFile: File
    ): ApiResult<File> = withContext(Dispatchers.IO) {
        try {
            val token = prefs.githubToken.first()
            val request = Request.Builder()
                .url(artifactUrl)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Accept", "application/vnd.github+json")
                .build()

            val response = NetworkModule.downloadClient.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.byteStream()?.use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                }
                ApiResult.Success(destFile)
            } else {
                ApiResult.Error(response.code, "Download failed: ${response.message}")
            }
        } catch (e: Exception) {
            ApiResult.Error(message = e.message ?: "Download failed")
        }
    }

    // ─── LOCAL DB — Projects ───────────────────────────────────
    fun getAllProjects(): Flow<List<Project>> = projectDao.getAllProjects()
    suspend fun getProjectById(id: Long) = projectDao.getProjectById(id)
    suspend fun insertProject(project: Project) = projectDao.insertProject(project)
    suspend fun updateProject(project: Project) = projectDao.updateProject(project)
    suspend fun deleteProject(project: Project) = projectDao.deleteProject(project)
    suspend fun touchProject(id: Long) = projectDao.touchProject(id)
    suspend fun updateGithubInfo(id: Long, repoName: String, repoUrl: String) = projectDao.updateGithubInfo(id, repoName, repoUrl)

    // ─── LOCAL DB — Files ──────────────────────────────────────
    fun getFilesForProject(projectId: Long) = fileDao.getFilesForProject(projectId)
    suspend fun getFilesSync(projectId: Long) = fileDao.getFilesForProjectSync(projectId)
    suspend fun insertFile(file: ProjectFile) = fileDao.insertFile(file)
    suspend fun insertFiles(files: List<ProjectFile>) = fileDao.insertFiles(files)
    suspend fun updateFile(file: ProjectFile) = fileDao.updateFile(file)
    suspend fun updateFileContent(id: Long, content: String) = fileDao.updateFileContent(id, content)
    suspend fun deleteFile(file: ProjectFile) = fileDao.deleteFile(file)

    // ─── LOCAL DB — Builds ─────────────────────────────────────
    fun getAllBuilds(): Flow<List<BuildRecord>> = buildDao.getAllBuilds()
    fun getBuildsForProject(projectId: Long) = buildDao.getBuildsForProject(projectId)
    suspend fun insertBuild(build: BuildRecord) = buildDao.insertBuild(build)
    suspend fun updateBuild(build: BuildRecord) = buildDao.updateBuild(build)
    suspend fun getBuildByRunId(runId: Long) = buildDao.getBuildByRunId(runId)
    suspend fun updateBuildStatus(id: Long, status: String, conclusion: String, completedAt: Long) =
        buildDao.updateBuildStatus(id, status, conclusion, completedAt)
    suspend fun updateApkInfo(id: Long, url: String, localPath: String, size: Long) =
        buildDao.updateApkInfo(id, url, localPath, size)
    suspend fun pruneOldBuilds() = buildDao.pruneOldBuilds()
}
