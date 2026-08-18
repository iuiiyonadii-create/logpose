package com.uriel.logpose.core.di

import com.uriel.logpose.core.telecom.LogPoseTelecom
import com.uriel.logpose.core.telecom.ScoStateManager
import com.uriel.logpose.core.thamis.EventBus
import com.uriel.logpose.data.memory.AgentMemoryDao
import com.uriel.logpose.data.memory.MemoryDao
import com.uriel.logpose.data.memory.MemoryDatabase
import com.uriel.logpose.features.settings.SettingsManager
import com.uriel.logpose.features.voice.PlaybackAwareMicGate
import com.uriel.logpose.features.voice.VoskVoiceEngine
import com.uriel.logpose.features.service.ActionManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoint {
    fun eventBus(): EventBus
    fun memoryDatabase(): MemoryDatabase
    fun memoryDao(): MemoryDao
    fun agentMemoryDao(): AgentMemoryDao
    fun settingsManager(): SettingsManager
    fun voskVoiceEngine(): VoskVoiceEngine
    fun playbackAwareMicGate(): PlaybackAwareMicGate
    fun logPoseTelecom(): LogPoseTelecom
    fun scoStateManager(): ScoStateManager
    fun pcBridge(): com.uriel.logpose.core.network.PCBridge
    fun appLauncher(): com.uriel.logpose.core.app.AppLauncher
    fun actionManager(): ActionManager
    fun bluetoothCommunicationManager(): com.uriel.logpose.core.services.BluetoothCommunicationManager
}
