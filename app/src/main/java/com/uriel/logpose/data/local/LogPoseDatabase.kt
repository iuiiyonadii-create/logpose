package com.uriel.logpose.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserPreferencesEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LogPoseDatabase : RoomDatabase() {
}
