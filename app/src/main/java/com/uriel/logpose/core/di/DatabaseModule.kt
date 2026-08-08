package com.uriel.logpose.core.di

import android.content.Context
import androidx.room.Room
import com.uriel.logpose.data.local.LogPoseDatabase
import com.uriel.logpose.data.memory.AgentMemoryDao
import com.uriel.logpose.data.memory.MemoryDao
import com.uriel.logpose.data.memory.MemoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLogPoseDatabase(@ApplicationContext context: Context): LogPoseDatabase {
        return Room.databaseBuilder(
            context,
            LogPoseDatabase::class.java,
            "logpose_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMemoryDatabase(@ApplicationContext context: Context): MemoryDatabase {
        return Room.databaseBuilder(
            context,
            MemoryDatabase::class.java,
            "thamis_memory"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMemoryDao(database: MemoryDatabase): MemoryDao {
        return database.memoryDao()
    }

    @Provides
    @Singleton
    fun provideAgentMemoryDao(database: MemoryDatabase): AgentMemoryDao {
        return database.agentMemoryDao()
    }
}
