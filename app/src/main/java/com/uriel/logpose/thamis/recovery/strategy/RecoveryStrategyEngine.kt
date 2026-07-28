package com.uriel.logpose.thamis.recovery.strategy

import com.uriel.logpose.thamis.monitoring.model.AnomalyType
import com.uriel.logpose.thamis.recovery.model.RecoveryStrategy

/**
 * Motor encargado de seleccionar la estrategia de recuperación óptima.
 */
object RecoveryStrategyEngine {

    fun select(anomalyType: AnomalyType): RecoveryStrategy {
        return when (anomalyType) {
            AnomalyType.HIGH_LATENCY -> RecoveryStrategy.WAIT
            AnomalyType.EXCESSIVE_RETRIES -> RecoveryStrategy.ESCALATE
            AnomalyType.INCONSISTENT_CONTEXT -> RecoveryStrategy.REBUILD
            AnomalyType.INVALID_SNAPSHOT -> RecoveryStrategy.RESET
            AnomalyType.TIMEOUT -> RecoveryStrategy.RECONNECT
            else -> RecoveryStrategy.RETRY
        }
    }
}
