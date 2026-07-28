package com.uriel.logpose.core.app

import android.content.Context
import androidx.room.Room
import com.uriel.logpose.core.bluetooth.BluetoothManager
import com.uriel.logpose.features.settings.SettingsManager
import com.uriel.logpose.features.settings.SettingsSession
import com.uriel.logpose.data.preferences.SettingsPreferences
import com.uriel.logpose.features.voice.VoskVoiceEngine
import com.uriel.logpose.features.voice.PlaybackAwareMicGate
import com.uriel.logpose.core.telecom.LogPoseTelecom
import com.uriel.logpose.core.services.BluetoothCommunicationManager
import android.media.AudioManager
import com.uriel.logpose.core.commands.CommandExecutor
import com.uriel.logpose.core.commands.CommandParser
import com.uriel.logpose.core.music.MusicController
import com.uriel.logpose.core.safety.SafetyEngine
import com.uriel.logpose.core.thamis.EventBus
import com.uriel.logpose.core.voice.VoiceEngine
import com.uriel.logpose.data.bluetooth.BluetoothRepositoryImpl
import com.uriel.logpose.data.memory.MemoryDatabase
import com.uriel.logpose.data.music.MusicRepositoryImpl
import com.uriel.logpose.data.notifications.NotificationRepositoryImpl
import com.uriel.logpose.data.voice.VoiceRepositoryImpl
import com.uriel.logpose.domain.repositories.BluetoothRepository
import com.uriel.logpose.domain.repositories.MusicRepository
import com.uriel.logpose.domain.repositories.NotificationRepository
import com.uriel.logpose.domain.repositories.VoiceRepository
import com.uriel.logpose.thamis_ai.decision.DecisionEngine
import com.uriel.logpose.thamis_ai.learning.LearningEngine
import com.uriel.logpose.thamis_ai.nlu.NaturalLanguageEngine
import com.uriel.logpose.thamis_ai.voice.VoiceInteractionManager
import com.uriel.logpose.thamis_ai.proactive.ProactiveAssistant
import com.uriel.logpose.thamis_ai.analytics.AnalyticsManager
import com.uriel.logpose.thamis_ai.security.SecurityManager
import com.uriel.logpose.thamis_ai.ai_models.AIModelManager
import com.uriel.logpose.thamis.thamis_final.ThamisCore

import com.uriel.logpose.feature.service.ActionManager

/**
 * Manual Dependency Injection container for LogPose MVP.
 */
class AppContainer(val context: Context) {

    val actionManager: ActionManager by lazy { ActionManager(musicController) }

    val thamisCore: ThamisCore by lazy { ThamisCore.getInstance(context) }
    val eventBus: EventBus by lazy { EventBus() }
    val safetyEngine: SafetyEngine by lazy { SafetyEngine() }
    val musicController: MusicController by lazy { MusicController(context) }
    val commandParser: CommandParser by lazy { CommandParser() }
    val commandExecutor: CommandExecutor by lazy { CommandExecutor(musicController) }

    val bluetoothManager: BluetoothManager by lazy {
        BluetoothManager(context)
    }

    val voiceEngine: VoiceEngine by lazy {
        VoiceEngine(context)
    }

    val memoryDatabase: MemoryDatabase by lazy {
        Room.databaseBuilder(context, MemoryDatabase::class.java, "thamis_memory").build()
    }

    val learningEngine: LearningEngine by lazy { LearningEngine() }
    val nluEngine: NaturalLanguageEngine by lazy { NaturalLanguageEngine() }
    val decisionEngine: DecisionEngine by lazy { DecisionEngine() }
    val voiceInteractionManager: VoiceInteractionManager by lazy { VoiceInteractionManager(context) }
    val proactiveAssistant: ProactiveAssistant by lazy { ProactiveAssistant() }
    val analyticsManager: AnalyticsManager by lazy { AnalyticsManager() }
    val securityManager: SecurityManager by lazy { SecurityManager() }
    val aiModelManager: AIModelManager by lazy { AIModelManager() }

    val settingsManager: SettingsManager by lazy {
        SettingsManager(SettingsPreferences(context), SettingsSession()).apply { start() }
    }

    val voskEngine: VoskVoiceEngine by lazy {
        VoskVoiceEngine(context)
    }

    val micGate: PlaybackAwareMicGate by lazy {
        PlaybackAwareMicGate()
    }

    val telecom: LogPoseTelecom by lazy {
        LogPoseTelecom(context, context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
    }

    val communicationManager: BluetoothCommunicationManager by lazy {
        BluetoothCommunicationManager(context)
    }

    val bluetoothRepository: BluetoothRepository by lazy {
        BluetoothRepositoryImpl(context)
    }

    val musicRepository: MusicRepository by lazy {
        MusicRepositoryImpl(context) as MusicRepository
    }

    val voiceRepository: VoiceRepository by lazy {
        VoiceRepositoryImpl() as VoiceRepository
    }

    val notificationRepository: NotificationRepository by lazy {
        NotificationRepositoryImpl()
    }

    companion object {
        lateinit var instance: AppContainer

        fun init(context: Context) {
            instance = AppContainer(context)
        }

        val appContext: Context get() = instance.context
        val settingsManager: SettingsManager get() = instance.settingsManager
        val voskEngine: VoskVoiceEngine get() = instance.voskEngine
        val micGate: PlaybackAwareMicGate get() = instance.micGate
        val telecom: LogPoseTelecom get() = instance.telecom
        val communicationManager: BluetoothCommunicationManager get() = instance.communicationManager
        val bluetoothRepository: BluetoothRepository get() = instance.bluetoothRepository
        val musicRepository: MusicRepository get() = instance.musicRepository
        val voiceRepository: VoiceRepository get() = instance.voiceRepository
        val notificationRepository: NotificationRepository get() = instance.notificationRepository
        val bluetoothManager: BluetoothManager get() = instance.bluetoothManager
        val musicController: MusicController get() = instance.musicController
        val actionManager: ActionManager get() = instance.actionManager
    }
}
