package com.uriel.logpose.core.di

import com.uriel.logpose.core.bluetooth.BluetoothManager
import com.uriel.logpose.core.commands.CommandExecutor
import com.uriel.logpose.core.commands.CommandParser
import com.uriel.logpose.core.music.MusicController
import com.uriel.logpose.core.safety.SafetyEngine
import com.uriel.logpose.core.services.BluetoothCommunicationManager
import com.uriel.logpose.core.telecom.LogPoseTelecom
import com.uriel.logpose.core.thamis.EventBus
import com.uriel.logpose.core.voice.VoiceEngine
import com.uriel.logpose.data.memory.AgentMemoryDao
import com.uriel.logpose.data.memory.MemoryDao
import com.uriel.logpose.data.memory.MemoryDatabase
import com.uriel.logpose.feature.service.ActionManager
import com.uriel.logpose.features.settings.SettingsManager
import com.uriel.logpose.features.voice.PlaybackAwareMicGate
import com.uriel.logpose.features.voice.VoskVoiceEngine
import com.uriel.logpose.thamis_ai.ai_models.AIModelManager
import com.uriel.logpose.thamis_ai.analytics.AnalyticsManager
import com.uriel.logpose.thamis_ai.decision.DecisionEngine
import com.uriel.logpose.thamis_ai.learning.LearningEngine
import com.uriel.logpose.thamis_ai.nlu.NaturalLanguageEngine
import com.uriel.logpose.thamis_ai.proactive.ProactiveAssistant
import com.uriel.logpose.thamis_ai.security.SecurityManager
import com.uriel.logpose.thamis_ai.voice.VoiceInteractionManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoint {
    fun actionManager(): ActionManager
    fun eventBus(): EventBus
    fun musicController(): MusicController
    fun commandParser(): CommandParser
    fun commandExecutor(): CommandExecutor
    fun safetyEngine(): SafetyEngine
    fun bluetoothManager(): BluetoothManager
    fun voiceEngine(): VoiceEngine
    fun memoryDatabase(): MemoryDatabase
    fun memoryDao(): MemoryDao
    fun agentMemoryDao(): AgentMemoryDao
    fun learningEngine(): LearningEngine
    fun naturalLanguageEngine(): NaturalLanguageEngine
    fun decisionEngine(): DecisionEngine
    fun voiceInteractionManager(): VoiceInteractionManager
    fun proactiveAssistant(): ProactiveAssistant
    fun analyticsManager(): AnalyticsManager
    fun securityManager(): SecurityManager
    fun aiModelManager(): AIModelManager
    fun settingsManager(): SettingsManager
    fun voskVoiceEngine(): VoskVoiceEngine
    fun playbackAwareMicGate(): PlaybackAwareMicGate
    fun logPoseTelecom(): LogPoseTelecom
    fun bluetoothCommunicationManager(): BluetoothCommunicationManager
    fun pcBridge(): com.uriel.logpose.core.network.PCBridge
    fun appLauncher(): com.uriel.logpose.core.app.AppLauncher
}
