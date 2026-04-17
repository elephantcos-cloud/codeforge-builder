package com.codeforge.builder

import android.app.Application
import androidx.work.Configuration
import com.codeforge.builder.data.local.AppDatabase

class CodeForgeApp : Application(), Configuration.Provider {

    companion object {
        lateinit var instance: CodeForgeApp
            private set
    }

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
