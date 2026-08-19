package com.uriel.logpose.thamis.monitoring.recovery

import com.uriel.logpose.thamis.monitoring.model.Anomaly
import com.uriel.logpose.thamis.monitoring.model.AnomalyType
import com.uriel.logpose.thamis.monitoring.model.RecoverySuggestion

/**
 * Sugiere acciones correctivas basadas en las anomalías detectadas.
 */
object RecoveryAdvisor {

    fun suggestRecoveries(anomalies: List<Anomaly>): List<RecoverySuggestion> {
        return anomalies.map { anomaly ->
            when (anomaly.type) {
                AnomalyType.HIGH_LATENCY -> RecoverySuggestion(
                    targetModule = anomaly.module,
                    action = "LIMPIAR_MEMORIA_CACHE",
                    reason = "Latencia excesiva",
                    expectedImpact = "Reducción de tiempo de respuesta"
                )
                AnomalyType.INVALID_SNAPSHOT -> RecoverySuggestion(
                    targetModule = "WorldModel",
                    action = "RECREAR_SNAPSHOT",
                    reason = "Corrupción de datos detectada",
                    expectedImpact = "Consistencia de estado"
                )
                else -> RecoverySuggestion(
                    targetModule = anomaly.module,
                    action = "REINICIAR_MODULO",
                    reason = "Estado desconocido",
                    expectedImpact = "Estabilidad"
                )
            }
        }
    }
}
