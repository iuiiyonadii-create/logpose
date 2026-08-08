package com.uriel.logpose.core.di

import android.content.Context
import android.media.AudioManager
import com.uriel.logpose.core.app.AppLauncher
import com.uriel.logpose.core.app.AppLauncherImpl
import com.uriel.logpose.core.bluetooth.BluetoothManager
import com.uriel.logpose.core.commands.CommandExecutor
import com.uriel.logpose.core.commands.CommandParser
import com.uriel.logpose.core.music.MusicController
import com.uriel.logpose.core.safety.SafetyEngine
import com.uriel.logpose.core.services.BluetoothCommunicationManager
import com.uriel.logpose.core.telecom.LogPoseTelecom
import com.uriel.logpose.core.thamis.EventBus
import com.uriel.logpose.core.voice.VoiceEngine
import com.uriel.logpose.data.preferences.SettingsPreferences
import com.uriel.logpose.feature.service.ActionManager
import com.uriel.logpose.features.settings.SettingsManager
import com.uriel.logpose.features.settings.SettingsSession
import com.uriel.logpose.features.voice.PlaybackAwareMicGate
import com.uriel.logpose.features.voice.VoskVoiceEngine
import com.uriel.logpose.thamis.thamis_final.ThamisCore
import com.uriel.logpose.thamis_ai.ai_models.AIModelManager
import com.uriel.logpose.thamis_ai.analytics.AnalyticsManager
import com.uriel.logpose.thamis_ai.decision.DecisionEngine
import com.uriel.logpose.thamis_ai.learning.LearningEngine
import com.uriel.logpose.thamis_ai.nlu.NaturalLanguageEngine
import com.uriel.logpose.thamis_ai.proactive.ProactiveAssistant
import com.uriel.logpose.thamis_ai.security.SecurityManager
import com.uriel.logpose.thamis_ai.voice.VoiceInteractionManager
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

    @Provides
    @Singleton
    fun provideVoskVoiceEngine(@ApplicationContext context: Context): VoskVoiceEngine {
        return VoskVoiceEngine(context)
    }

    @Provides
    @Singleton
    fun provideBluetoothCommunicationManager(@ApplicationContext context: Context): BluetoothCommunicationManager {
        return BluetoothCommunicationManager(context)
    }

    @Provides
    @Singleton
    fun provideLogPoseTelecom(@ApplicationContext context: Context): LogPoseTelecom {
        return LogPoseTelecom(context, context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
    }

    @Provides
    @Singleton
    fun provideBluetoothManager(@ApplicationContext context: Context): BluetoothManager {
        return BluetoothManager(context)
    }

    @Provides
    @Singleton
    fun provideActionManager(musicController: MusicController): ActionManager {
        return ActionManager(musicController)
    }

    @Provides
    @Singleton
    fun provideVoiceEngine(@ApplicationContext context: Context): VoiceEngine {
        return VoiceEngine(context)
    }

    @Provides
    @Singleton
    fun provideLearningEngine(): LearningEngine {
        return LearningEngine()
    }

    @Provides
    @Singleton
    fun provideNaturalLanguageEngine(): NaturalLanguageEngine {
        return NaturalLanguageEngine()
    }

    @Provides
    @Singleton
    fun provideDecisionEngine(): DecisionEngine {
        return DecisionEngine()
    }

    @Provides
    @Singleton
    fun provideVoiceInteractionManager(@ApplicationContext context: Context): VoiceInteractionManager {
        return VoiceInteractionManager(context)
    }

    @Provides
    @Singleton
    fun provideProactiveAssistant(): ProactiveAssistant {
        return ProactiveAssistant()
    }

    @Provides
    @Singleton
    fun provideAnalyticsManager(): AnalyticsManager {
        return AnalyticsManager()
    }

    @Provides
    @Singleton
    fun provideSecurityManager(): SecurityManager {
        return SecurityManager()
    }

    @Provides
    @Singleton
    fun provideAIModelManager(): AIModelManager {
        return AIModelManager()
    }

    @Provides
    @Singleton
    fun provideSettingsManager(@ApplicationContext context: Context): SettingsManager {
        return SettingsManager(SettingsPreferences(context), SettingsSession()).apply { start() }
    }

    @Provides
    @Singleton
    fun providePlaybackAwareMicGate(): PlaybackAwareMicGate {
        return PlaybackAwareMicGate()
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
}
