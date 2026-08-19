package com.uriel.logpose.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserPreferencesEntity::class,
        DriverPreferenceEntity::class,
        LearnedPhoneticEntity::class,
        CommandMaturityEntity::class,
        UserCorrectionEntity::class,
        ActionFrequencyEntity::class,
        LearnedEntityRecord::class,
        TrackToArtistEntity::class,
        WorldSnapshotCheckpointEntity::class,
        FavoriteArtistEntity::class,
        com.uriel.logpose.data.memory.AgentMemoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LogPoseDatabase : RoomDatabase() {
    abstract fun preferencesDao(): PreferencesDao
    abstract fun logPoseDao(): LogPoseDao
    abstract fun agentMemoryDao(): com.uriel.logpose.data.memory.AgentMemoryDao
}
