package com.uriel.logpose.core.services.guardians

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.utils.BatteryMonitor
import com.uriel.logpose.features.voice.VoskVoiceEngine
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BatteryGuardian @Inject constructor(
    private val batteryMonitor: BatteryMonitor,
    private val voskEngine: VoskVoiceEngine
) {
    private var isBatteryLow = false
    private val BATTERY_THRESHOLD_LOW = 20
    private val BATTERY_THRESHOLD_NORMAL = 25
    private var pollingJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun startMonitoring(onUpdate: (Int) -> Unit) {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            while (isActive) {
                val phonePct = batteryMonitor.phoneBatteryPct
                onUpdate(phonePct)

                if (!isBatteryLow && phonePct <= BATTERY_THRESHOLD_LOW) {
                    isBatteryLow = true
                    LogPoseLogger.w("ThamisBattery: Entrando en modo BATERÍA BAJA ($phonePct%)")
                    voskEngine.setPowerSaveMode(true)
                } else if (isBatteryLow && phonePct >= BATTERY_THRESHOLD_NORMAL) {
                    isBatteryLow = false
                    LogPoseLogger.i("ThamisBattery: Restaurando modo NORMAL ($phonePct%)")
                    voskEngine.setPowerSaveMode(false)
                }

                delay(60000L)
            }
        }
    }

    fun stopMonitoring() {
        pollingJob?.cancel()
    }
}
