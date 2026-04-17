package com.codeforge.builder.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.codeforge.builder.MainActivity
import com.codeforge.builder.R
import com.codeforge.builder.data.remote.model.ApiResult
import com.codeforge.builder.data.repository.GitHubRepository
import com.codeforge.builder.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class BuildMonitorWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "build_status"
        const val CHANNEL_NAME = "Build Status"
        const val NOTIF_ID_RUNNING = 1001
        const val NOTIF_ID_DONE = 1002

        fun start(
            context: Context,
            runId: Long,
            repoOwner: String,
            repoName: String,
            buildRecordId: Long
        ) {
            val data = workDataOf(
                Constants.WORK_DATA_RUN_ID to runId,
                Constants.WORK_DATA_REPO_OWNER to repoOwner,
                Constants.WORK_DATA_REPO_NAME to repoName,
                Constants.WORK_DATA_BUILD_RECORD_ID to buildRecordId
            )
            val request = OneTimeWorkRequestBuilder<BuildMonitorWorker>()
                .setInputData(data)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .addTag(Constants.WORK_TAG_BUILD_MONITOR)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "${Constants.WORK_TAG_BUILD_MONITOR}_$runId",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    private val repo = GitHubRepository(applicationContext)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val runId = inputData.getLong(Constants.WORK_DATA_RUN_ID, -1L)
        val owner = inputData.getString(Constants.WORK_DATA_REPO_OWNER) ?: return@withContext Result.failure()
        val repoName = inputData.getString(Constants.WORK_DATA_REPO_NAME) ?: return@withContext Result.failure()
        val buildId = inputData.getLong(Constants.WORK_DATA_BUILD_RECORD_ID, -1L)

        if (runId == -1L || buildId == -1L) return@withContext Result.failure()

        createNotificationChannel()
        showRunningNotification()

        var retries = 0
        while (retries < Constants.MAX_POLL_RETRIES) {
            delay(Constants.WORK_POLL_INTERVAL_SECONDS * 1000)

            val result = repo.getWorkflowRun(owner, repoName, runId)
            if (result is ApiResult.Success) {
                val run = result.data
                if (run.status == "completed") {
                    val conclusion = run.conclusion ?: "unknown"
                    repo.updateBuildStatus(buildId, "completed", conclusion, System.currentTimeMillis())

                    if (conclusion == "success") {
                        // Try to get artifact info
                        val artifactsResult = repo.getArtifacts(owner, repoName, runId)
                        if (artifactsResult is ApiResult.Success) {
                            val apkArtifact = artifactsResult.data.firstOrNull {
                                it.name.startsWith("app-debug") && !it.expired
                            }
                            if (apkArtifact != null) {
                                repo.updateApkInfo(buildId, apkArtifact.archiveDownloadUrl, "", apkArtifact.sizeInBytes)
                            }
                        }
                        showSuccessNotification(repoName)
                    } else {
                        showFailureNotification(repoName, conclusion)
                    }
                    return@withContext Result.success()
                }
            }
            retries++
        }

        // Timeout
        repo.updateBuildStatus(buildId, "completed", "timed_out", System.currentTimeMillis())
        return@withContext Result.failure()
    }

    private fun createNotificationChannel() {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Shows APK build status from GitHub Actions"
            }
            manager.createNotificationChannel(channel)
        }
    }

    private fun showRunningNotification() {
        val notif = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Building APK...")
            .setContentText("GitHub Actions is building your project")
            .setOngoing(true)
            .setProgress(0, 0, true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIF_ID_RUNNING, notif)
    }

    private fun showSuccessNotification(repoName: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "builds")
        }
        val pi = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notif = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Build Successful!")
            .setContentText("$repoName APK is ready to download")
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(NOTIF_ID_RUNNING)
        manager.notify(NOTIF_ID_DONE, notif)
    }

    private fun showFailureNotification(repoName: String, conclusion: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "builds")
        }
        val pi = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notif = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Build $conclusion")
            .setContentText("$repoName build did not succeed. Tap to view logs.")
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(NOTIF_ID_RUNNING)
        manager.notify(NOTIF_ID_DONE, notif)
    }
}
