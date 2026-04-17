package com.codeforge.builder.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ─────────────────────────────────────
//  Project Entity
// ─────────────────────────────────────
@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val type: String,              // HTML/CSS/JS, Kotlin, Java
    val packageName: String,       // e.g. com.example.myapp
    val appName: String,           // Display name
    val githubRepoName: String = "",
    val githubRepoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastBuildStatus: String = "",
    val lastBuildAt: Long = 0L,
    val isGithubConnected: Boolean = false
)

// ─────────────────────────────────────
//  Project File Entity
// ─────────────────────────────────────
@Entity(
    tableName = "project_files",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId")]
)
data class ProjectFile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val fileName: String,           // e.g. "index.html"
    val filePath: String,           // Relative path e.g. "assets/index.html"
    val content: String = "",
    val language: String = "",      // html, css, js, kotlin, java, xml
    val isMainFile: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────
//  Build Record Entity
// ─────────────────────────────────────
@Entity(tableName = "build_records")
data class BuildRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val projectName: String,
    val githubRunId: Long = 0L,
    val githubRunUrl: String = "",
    val githubRepoOwner: String = "",
    val githubRepoName: String = "",
    val status: String = "queued",       // queued, in_progress, completed
    val conclusion: String = "",         // success, failure, cancelled, skipped
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long = 0L,
    val apkDownloadUrl: String = "",
    val apkLocalPath: String = "",
    val apkSize: Long = 0L,
    val buildNumber: Int = 0,
    val commitMessage: String = "",
    val errorMessage: String = ""
) {
    val isCompleted: Boolean get() = status == "completed"
    val isSuccess: Boolean get() = conclusion == "success"
    val isRunning: Boolean get() = status == "in_progress" || status == "queued"
    val durationMs: Long get() = if (completedAt > 0) completedAt - startedAt else 0L
    val hasApk: Boolean get() = apkLocalPath.isNotEmpty() || apkDownloadUrl.isNotEmpty()
}
