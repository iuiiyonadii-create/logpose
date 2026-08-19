package com.uriel.logpose.thamis.lab.chaos

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Motor para introducir fallos y latencias artificiales en el entorno simulado.
 */
object ChaosController {

    fun injectLatency(module: String, durationMs: Long) {
        LogPoseLogger.w("THAMIS_CHAOS: Inyectando latencia de ${durationMs}ms en módulo: $module")
    }

    fun forceFailure(module: String, errorCode: String) {
        LogPoseLogger.e("THAMIS_CHAOS: Forzando fallo crítico ($errorCode) en módulo: $module")
    }

    fun simulateSignalLoss(sensor: String) {
        LogPoseLogger.w("THAMIS_CHAOS: Simulando pérdida de señal en sensor: $sensor")
    }
}
