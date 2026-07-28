package com.uriel.logpose.thamis.validation.audio

import com.uriel.logpose.core.compat.core.LogPoseLogger
import java.util.concurrent.atomic.AtomicReference

/**
 * Validador del ciclo de vida de SCO.
 * Monitorea que el canal de audio se abra y cierre correctamente.
 */
object ScoLifecycleValidator {
    private const val TAG = "THAMIS_AUDIO"
    
    private val currentState = AtomicReference<AudioRoutingTrace.ScoState>(AudioRoutingTrace.ScoState.SCO_DISCONNECTED)
    private var startTime: Long = 0

    fun notifyStateChange(newState: AudioRoutingTrace.ScoState) {
        val oldState = currentState.getAndSet(newState)
        
        when (newState) {
            AudioRoutingTrace.ScoState.SCO_OPENING -> {
                startTime = System.currentTimeMillis()
                LogPoseLogger.i("[$TAG] SCO_OPEN")
            }
            AudioRoutingTrace.ScoState.LISTENING -> {
                LogPoseLogger.i("[$TAG] VOICE_CAPTURE_STARTED")
            }
            AudioRoutingTrace.ScoState.REASONING -> {
                LogPoseLogger.i("[$TAG] DECISION_STARTED")
            }
            AudioRoutingTrace.ScoState.EXECUTING -> {
                LogPoseLogger.i("[$TAG] DECISION_COMPLETE")
            }
            AudioRoutingTrace.ScoState.SCO_RELEASED -> {
                val duration = System.currentTimeMillis() - startTime
                LogPoseLogger.i("[$TAG] SCO_RELEASED")
                LogPoseLogger.i("[$TAG] SCO lifecycle OK | Total=${duration}ms")
            }
            else -> {}
        }

        // Regla: No puede quedar abierto > 5s sin entrada (simulado por ahora)
        if (newState == AudioRoutingTrace.ScoState.SCO_CONNECTED && System.currentTimeMillis() - startTime > 5000) {
            LogPoseLogger.w("[$TAG] THAMIS_AUDIO_WARNING: SCO active for > 5s without voice capture")
        }
    }

    fun getCurrentState(): AudioRoutingTrace.ScoState = currentState.get()
}
