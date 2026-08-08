package com.uriel.logpose.core.di

import com.uriel.logpose.data.bluetooth.BluetoothRepositoryImpl
import com.uriel.logpose.data.music.MusicRepositoryImpl
import com.uriel.logpose.data.notifications.NotificationRepositoryImpl
import com.uriel.logpose.data.voice.VoiceRepositoryImpl
import com.uriel.logpose.domain.repositories.BluetoothRepository
import com.uriel.logpose.domain.repositories.MusicRepository
import com.uriel.logpose.domain.repositories.NotificationRepository
import com.uriel.logpose.domain.repositories.VoiceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBluetoothRepository(
        bluetoothRepositoryImpl: BluetoothRepositoryImpl
    ): BluetoothRepository

    @Binds
    @Singleton
    abstract fun bindMusicRepository(
        musicRepositoryImpl: MusicRepositoryImpl
    ): MusicRepository

    @Binds
    @Singleton
    abstract fun bindVoiceRepository(
        voiceRepositoryImpl: VoiceRepositoryImpl
    ): VoiceRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
}
