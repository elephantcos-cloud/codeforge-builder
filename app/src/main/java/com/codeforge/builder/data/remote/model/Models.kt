package com.codeforge.builder.data.remote.model

import com.google.gson.annotations.SerializedName

// ── GitHub User ──────────────────────────────
data class GitHubUser(
    val login: String = "",
    val name: String? = "",
    val email: String? = "",
    @SerializedName("avatar_url") val avatarUrl: String = "",
    @SerializedName("public_repos") val publicRepos: Int = 0
)

// ── Repository ───────────────────────────────
data class GitHubRepo(
    val id: Long = 0,
    val name: String = "",
    @SerializedName("full_name") val fullName: String = "",
    @SerializedName("html_url") val htmlUrl: String = "",
    @SerializedName("clone_url") val cloneUrl: String = "",
    val private: Boolean = false,
    val description: String? = null
)

data class CreateRepoRequest(
    val name: String,
    val description: String = "Created with CodeForge",
    val private: Boolean = false,
    @SerializedName("auto_init") val autoInit: Boolean = true
)

// ── Contents API ─────────────────────────────
data class ContentResponse(
    val name: String = "",
    val path: String = "",
    val sha: String = "",
    val content: String? = null,
    @SerializedName("download_url") val downloadUrl: String? = null
)

data class CreateFileRequest(
    val message: String,
    val content: String,         // base64 encoded
    val sha: String? = null,     // required for updates
    val branch: String = "main",
    val committer: CommitAuthor? = null
)

data class CommitAuthor(
    val name: String,
    val email: String
)

data class CreateFileResponse(
    val content: ContentResponse? = null
)

// ── Actions Workflow Run ──────────────────────
data class WorkflowRunsResponse(
    @SerializedName("total_count") val totalCount: Int = 0,
    @SerializedName("workflow_runs") val workflowRuns: List<WorkflowRun> = emptyList()
)

data class WorkflowRun(
    val id: Long = 0,
    val name: String? = "",
    val status: String = "",         // queued, in_progress, completed
    val conclusion: String? = null,  // success, failure, cancelled
    @SerializedName("html_url") val htmlUrl: String = "",
    @SerializedName("run_number") val runNumber: Int = 0,
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("updated_at") val updatedAt: String = "",
    @SerializedName("head_commit") val headCommit: HeadCommit? = null
)

data class HeadCommit(
    val id: String = "",
    val message: String = "",
    val author: CommitAuthor? = null
)

// ── Artifacts ────────────────────────────────
data class ArtifactsResponse(
    @SerializedName("total_count") val totalCount: Int = 0,
    val artifacts: List<Artifact> = emptyList()
)

data class Artifact(
    val id: Long = 0,
    val name: String = "",
    @SerializedName("size_in_bytes") val sizeInBytes: Long = 0,
    @SerializedName("archive_download_url") val archiveDownloadUrl: String = "",
    @SerializedName("expired") val expired: Boolean = false,
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("expires_at") val expiresAt: String = ""
)

// ── Sealed Result ─────────────────────────────
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int = -1, val message: String) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}
