package com.uriel.logpose.core.di

import android.content.Context
import com.uriel.logpose.core.commands.CommandExecutor
import com.uriel.logpose.core.commands.CommandParser
import com.uriel.logpose.core.music.MusicController
import com.uriel.logpose.core.safety.SafetyEngine
import com.uriel.logpose.core.thamis.EventBus
import com.uriel.logpose.thamis.thamis_final.ThamisCore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideThamisCore(@ApplicationContext context: Context): ThamisCore {
        return ThamisCore.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideMusicController(@ApplicationContext context: Context): MusicController {
        return MusicController(context)
    }

    @Provides
    @Singleton
    fun provideCommandParser(): CommandParser {
        return CommandParser()
    }

    @Provides
    @Singleton
    fun provideCommandExecutor(musicController: MusicController): CommandExecutor {
        return CommandExecutor(musicController)
    }

    @Provides
    @Singleton
    fun provideSafetyEngine(): SafetyEngine {
        return SafetyEngine()
    }

    @Provides
    @Singleton
    fun provideEventBus(): EventBus {
        return EventBus()
    }
}
