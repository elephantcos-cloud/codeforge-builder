package com.codeforge.builder.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.codeforge.builder.data.local.entity.BuildRecord
import com.codeforge.builder.data.local.entity.Project
import com.codeforge.builder.data.local.entity.ProjectFile
import kotlinx.coroutines.flow.Flow

// ─────────────────────────────────────
//  Project DAO
// ─────────────────────────────────────
@Dao
interface ProjectDao {

    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): Project?

    @Query("SELECT * FROM projects WHERE type = :type ORDER BY updatedAt DESC")
    fun getProjectsByType(type: String): Flow<List<Project>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)

    @Query("UPDATE projects SET lastBuildStatus = :status, lastBuildAt = :buildAt WHERE id = :id")
    suspend fun updateBuildStatus(id: Long, status: String, buildAt: Long)

    @Query("UPDATE projects SET githubRepoName = :repoName, githubRepoUrl = :repoUrl, isGithubConnected = 1 WHERE id = :id")
    suspend fun updateGithubInfo(id: Long, repoName: String, repoUrl: String)

    @Query("UPDATE projects SET updatedAt = :updatedAt WHERE id = :id")
    suspend fun touchProject(id: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getProjectCount(): Int
}

// ─────────────────────────────────────
//  ProjectFile DAO
// ─────────────────────────────────────
@Dao
interface ProjectFileDao {

    @Query("SELECT * FROM project_files WHERE projectId = :projectId ORDER BY isMainFile DESC, fileName ASC")
    fun getFilesForProject(projectId: Long): Flow<List<ProjectFile>>

    @Query("SELECT * FROM project_files WHERE projectId = :projectId ORDER BY isMainFile DESC, fileName ASC")
    suspend fun getFilesForProjectSync(projectId: Long): List<ProjectFile>

    @Query("SELECT * FROM project_files WHERE id = :id")
    suspend fun getFileById(id: Long): ProjectFile?

    @Query("SELECT * FROM project_files WHERE projectId = :projectId AND fileName = :fileName LIMIT 1")
    suspend fun getFileByName(projectId: Long, fileName: String): ProjectFile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: ProjectFile): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(files: List<ProjectFile>)

    @Update
    suspend fun updateFile(file: ProjectFile)

    @Query("UPDATE project_files SET content = :content, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateFileContent(id: Long, content: String, updatedAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteFile(file: ProjectFile)

    @Query("DELETE FROM project_files WHERE projectId = :projectId")
    suspend fun deleteAllFilesForProject(projectId: Long)

    @Query("SELECT COUNT(*) FROM project_files WHERE projectId = :projectId")
    suspend fun getFileCount(projectId: Long): Int
}

// ─────────────────────────────────────
//  BuildRecord DAO
// ─────────────────────────────────────
@Dao
interface BuildRecordDao {

    @Query("SELECT * FROM build_records ORDER BY startedAt DESC")
    fun getAllBuilds(): Flow<List<BuildRecord>>

    @Query("SELECT * FROM build_records WHERE projectId = :projectId ORDER BY startedAt DESC")
    fun getBuildsForProject(projectId: Long): Flow<List<BuildRecord>>

    @Query("SELECT * FROM build_records WHERE id = :id")
    suspend fun getBuildById(id: Long): BuildRecord?

    @Query("SELECT * FROM build_records WHERE githubRunId = :runId LIMIT 1")
    suspend fun getBuildByRunId(runId: Long): BuildRecord?

    @Query("SELECT * FROM build_records WHERE status != 'completed' ORDER BY startedAt DESC")
    suspend fun getActiveBuildRecords(): List<BuildRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuild(build: BuildRecord): Long

    @Update
    suspend fun updateBuild(build: BuildRecord)

    @Query("UPDATE build_records SET status = :status, conclusion = :conclusion, completedAt = :completedAt WHERE id = :id")
    suspend fun updateBuildStatus(id: Long, status: String, conclusion: String, completedAt: Long)

    @Query("UPDATE build_records SET apkDownloadUrl = :url, apkLocalPath = :localPath, apkSize = :size WHERE id = :id")
    suspend fun updateApkInfo(id: Long, url: String, localPath: String, size: Long)

    @Delete
    suspend fun deleteBuild(build: BuildRecord)

    @Query("DELETE FROM build_records WHERE projectId = :projectId")
    suspend fun deleteBuildsForProject(projectId: Long)

    @Query("DELETE FROM build_records WHERE id NOT IN (SELECT id FROM build_records ORDER BY startedAt DESC LIMIT 50)")
    suspend fun pruneOldBuilds()

    @Query("SELECT COUNT(*) FROM build_records")
    suspend fun getBuildCount(): Int
}
