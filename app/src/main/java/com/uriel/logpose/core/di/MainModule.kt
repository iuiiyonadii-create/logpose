package com.uriel.logpose.core.di

import com.uriel.logpose.data.notifications.NotificationRepositoryImpl
import com.uriel.logpose.data.voice.VoiceRepositoryImpl
import com.uriel.logpose.domain.repositories.NotificationRepository
import com.uriel.logpose.domain.repositories.VoiceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MainModule {

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindVoiceRepository(
        impl: VoiceRepositoryImpl
    ): VoiceRepository
}
