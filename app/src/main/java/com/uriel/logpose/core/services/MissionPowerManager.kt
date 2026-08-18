package com.uriel.logpose.core.services

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.parser.LabDiscoveryService
import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.thamis.cognitive.CognitivePipeline
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MissionPowerManager: El Centinela de Energía Staff.
 * v74.0: Implementación de Modo Ahorro Extremo tras 5 minutos de Labs offline.
 */
@Singleton
class MissionPowerManager @Inject constructor() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var standbyJob: Job? = null
    private val STANDBY_TIMEOUT = 300_000L // 5 Minutos de gracia

    fun start() {
        LogPoseLogger.i("Power: Iniciando Centinela de Energía.")
        
        scope.launch {
            LabDiscoveryService.isLabPresent.collectLatest { isPresent ->
                if (isPresent) {
                    abortStandby()
                } else {
                    startStandbyCountdown()
                }
            }
        }
    }

    private fun startStandbyCountdown() {
        standbyJob?.cancel()
        standbyJob = scope.launch {
            LogPoseLogger.d("Power: Labs Offline. Iniciando cuenta regresiva de 5m para Ahorro Extremo.")
            delay(STANDBY_TIMEOUT)
            activateExtremeSaving()
        }
    }

    private fun abortStandby() {
        if (standbyJob?.isActive == true) {
            LogPoseLogger.i("Power: Labs reconectado. Abortando modo ahorro.")
        }
        standbyJob?.cancel()
        
        // Restauramos rendimiento Staff
        val entryPoint = LogPoseApplication.entryPoint
        entryPoint.voskVoiceEngine().setPowerSaveMode(false)
        entryPoint.pcBridge().setHeartbeatEnabled(true)
        CognitivePipeline.setLowPowerMode(false)
    }

    private fun activateExtremeSaving() {
        LogPoseLogger.w("Power: ACTIVANDO MODO AHORRO EXTREMO. Labs no detectado en 5m.")
        
        // 1. Reducimos carga de procesamiento de voz (Throttle de parciales)
        val entryPoint = LogPoseApplication.entryPoint
        entryPoint.voskVoiceEngine().setPowerSaveMode(true)
        
        // 2. Suspendemos latidos de red y telemetría UDP
        entryPoint.pcBridge().setHeartbeatEnabled(false)
        CognitivePipeline.setLowPowerMode(true)
        
        // 3. Feedback al Rider
        com.uriel.logpose.features.voice.FeedbackManager.speak("Ahorro de energía activado. Labs fuera de alcance.")
    }
}
