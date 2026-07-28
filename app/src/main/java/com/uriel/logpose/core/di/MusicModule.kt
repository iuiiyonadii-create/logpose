package com.uriel.logpose.core.di

import android.content.Context
import com.uriel.logpose.data.music.MusicRepositoryImpl
import com.uriel.logpose.domain.repositories.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicModule {

    @Provides
    @Singleton
    fun provideMusicRepository(
        @ApplicationContext context: Context
    ): MusicRepository {
        return MusicRepositoryImpl(context)
    }
}
