package com.uriel.logpose.core.di

import android.content.Context
import android.media.AudioManager
import com.uriel.logpose.core.app.AppLauncher
import com.uriel.logpose.core.app.AppLauncherImpl
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.telecom.LogPoseTelecom
import com.uriel.logpose.core.telecom.ScoStateManager
import com.uriel.logpose.core.thamis.EventBus
import com.uriel.logpose.data.preferences.SettingsPreferences
import com.uriel.logpose.features.service.ActionManager
import com.uriel.logpose.features.settings.SettingsManager
import com.uriel.logpose.features.settings.SettingsSession
import com.uriel.logpose.features.voice.PlaybackAwareMicGate
import com.uriel.logpose.features.voice.VoskVoiceEngine
import com.uriel.logpose.thamis.thamis_final.ThamisCore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EngineModule {

    @Provides
    @Singleton
    fun provideThamisCore(@ApplicationContext context: Context): ThamisCore {
        return ThamisCore.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideEventBus(): EventBus {
        return EventBus()
    }

    @Provides
    @Singleton
    fun provideVoskVoiceEngine(@ApplicationContext context: Context): VoskVoiceEngine {
        return VoskVoiceEngine(context)
    }

    @Provides
    @Singleton
    fun provideBluetoothManager(@ApplicationContext context: Context): com.uriel.logpose.features.bluetooth.BluetoothManager {
        return com.uriel.logpose.features.bluetooth.BluetoothManager(context)
    }

    @Provides
    @Singleton
    fun provideBluetoothCommunicationManager(@ApplicationContext context: Context): com.uriel.logpose.core.services.BluetoothCommunicationManager {
        return com.uriel.logpose.core.services.BluetoothCommunicationManager(context)
    }

    @Provides
    @Singleton
    fun provideLogPoseTelecom(@ApplicationContext context: Context): LogPoseTelecom {
        return LogPoseTelecom(context, context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
    }

    @Provides
    @Singleton
    fun provideMusicController(@ApplicationContext context: Context): com.uriel.logpose.core.music.MusicController {
        return com.uriel.logpose.core.music.MusicController(context)
    }

    @Provides
    @Singleton
    fun provideActionManager(musicController: com.uriel.logpose.core.music.MusicController): ActionManager {
        return ActionManager(musicController)
    }

    @Provides
    @Singleton
    fun provideSettingsManager(@ApplicationContext context: Context): SettingsManager {
        return SettingsManager(SettingsPreferences(context), SettingsSession()).apply { start() }
    }

    @Provides
    @Singleton
    fun providePlaybackAwareMicGate(@ApplicationContext context: Context): PlaybackAwareMicGate {
        return PlaybackAwareMicGate(context)
    }

    @Provides
    @Singleton
    fun provideAppLauncher(@ApplicationContext context: Context): AppLauncher {
        return AppLauncherImpl(context)
    }

    @Provides
    @Singleton
    fun provideBatteryMonitor(@ApplicationContext context: Context): com.uriel.logpose.core.utils.BatteryMonitor {
        return com.uriel.logpose.core.utils.BatteryMonitor(context)
    }

    @Provides
    @Singleton
    fun provideScoStateManager(
        @ApplicationContext context: Context
    ): ScoStateManager {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return ScoStateManager(context, audioManager) {
            LogPoseLogger.i("SCO DI: Reconnection trigger active.")
        }
    }
}
