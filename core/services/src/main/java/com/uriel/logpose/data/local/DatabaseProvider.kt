package com.uriel.logpose.data.local

import android.content.Context
import androidx.room.Room
import com.uriel.logpose.core.compat.core.AppContextProvider

object DatabaseProvider {
    private var database: LogPoseDatabase? = null

    fun getDatabase(context: Context = AppContextProvider.applicationContext): LogPoseDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                LogPoseDatabase::class.java,
                "logpose.db"
            )
            .fallbackToDestructiveMigration()
            .build().also { database = it }
        }
    }

    fun setDatabase(db: LogPoseDatabase) {
        database = db
    }
}
