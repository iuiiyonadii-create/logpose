package com.uriel.logpose.thamis.recovery.action

import com.uriel.logpose.thamis.monitoring.model.Anomaly
import com.uriel.logpose.thamis.monitoring.model.DiagnosticReport
import com.uriel.logpose.thamis.recovery.model.RecoveryAction
import com.uriel.logpose.thamis.recovery.model.RecoveryPlan
import com.uriel.logpose.thamis.recovery.model.RecoveryStrategy

/**
 * Generador de planes de recuperación.
 */
object RecoveryPlanner {

    fun createPlan(anomaly: Anomaly, diagnostic: DiagnosticReport, strategy: RecoveryStrategy): RecoveryPlan {
        val actions = when (strategy) {
            RecoveryStrategy.RETRY -> listOf(
                RecoveryAction(description = "Reintentar operación", type = "RETRY", risk = 0.1f, expectedResult = "SUCCESS")
            )
            RecoveryStrategy.RECONNECT -> listOf(
                RecoveryAction(description = "Reiniciar conexión de proveedor", type = "RECONNECT", risk = 0.3f, expectedResult = "CONNECTED")
            )
            RecoveryStrategy.RESET -> listOf(
                RecoveryAction(description = "Reiniciar instancia del módulo", type = "RESET", risk = 0.6f, expectedResult = "INITIALIZED")
            )
            RecoveryStrategy.REBUILD -> listOf(
                RecoveryAction(description = "Reconstruir contexto cognitivo", type = "REBUILD", risk = 0.4f, expectedResult = "CONSISTENT")
            )
            else -> listOf(
                RecoveryAction(description = "Esperar estabilización", type = "WAIT", risk = 0.05f, expectedResult = "READY")
            )
        }

        return RecoveryPlan(
            targetModule = anomaly.module,
            anomaly = anomaly,
            diagnostic = diagnostic,
            strategy = strategy,
            actions = actions,
            priority = if (strategy == RecoveryStrategy.ESCALATE) 100 else 50,
            risk = actions.maxOf { it.risk },
            confidence = 0.9f
        )
    }
}
