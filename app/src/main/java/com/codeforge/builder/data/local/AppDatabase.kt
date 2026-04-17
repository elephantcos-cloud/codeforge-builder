package com.codeforge.builder.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.codeforge.builder.data.local.dao.BuildRecordDao
import com.codeforge.builder.data.local.dao.ProjectDao
import com.codeforge.builder.data.local.dao.ProjectFileDao
import com.codeforge.builder.data.local.entity.BuildRecord
import com.codeforge.builder.data.local.entity.Project
import com.codeforge.builder.data.local.entity.ProjectFile
import com.codeforge.builder.utils.Constants

@Database(
    entities = [Project::class, ProjectFile::class, BuildRecord::class],
    version = Constants.DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun projectDao(): ProjectDao
    abstract fun projectFileDao(): ProjectFileDao
    abstract fun buildRecordDao(): BuildRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
