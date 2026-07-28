package com.uriel.logpose.core.di

import android.content.Context
import androidx.room.Room
import com.uriel.logpose.data.local.LogPoseDatabase
import com.uriel.logpose.data.local.PreferencesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LogPoseDatabase {
        return Room.databaseBuilder(
            context,
            LogPoseDatabase::class.java,
            "logpose_db"
        ).build()
    }

    // Example of DAO provision
    /*
    @Provides
    fun providePreferencesDao(database: LogPoseDatabase): PreferencesDao {
        return database.preferencesDao()
    }
    */
}
