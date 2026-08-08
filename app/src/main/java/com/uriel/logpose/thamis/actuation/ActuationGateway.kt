package com.uriel.logpose.thamis.actuation

import com.uriel.logpose.thamis.ThamisConfiguration
import com.uriel.logpose.thamis.cognitive.model.ThamisDecision
import com.uriel.logpose.thamis.cognitive.model.WorldState
import com.thamis.lab.core.contracts.intent.Intent

/**
 * La puerta de enlace para la actuación real de THAMIS.
 * Verifica la autoridad y traduce la decisión cognitiva a una intención de ejecución.
 */
object ActuationGateway {

    fun requestActuation(decision: ThamisDecision, worldState: WorldState): ActuationResult {
        // 1. Verificar Feature Flag Global
        if (!ThamisConfiguration.authorityEnabled) {
            return buildBlocked(decision, "Autoridad global desactivada")
        }

        // 2. Verificar Política de Autoridad (¿Está permitido este Intent?)
        if (!ThamisAuthorityPolicy.isAuthorized(decision.intent)) {
            return buildBlocked(decision, "${decision.intent} bloqueado: autoridad no disponible")
        }

        // 3. Validación específica para Música (Fase 13.1)
        if (decision.winningEvaluation?.hypothesis?.candidateGoal?.category == com.uriel.logpose.thamis.cognitive.model.Goal.Category.MULTIMEDIA) {
            if (!MusicAuthorityValidator.validate(decision, worldState)) {
                return buildBlocked(decision, "Validación de autoridad musical fallida")
            }
        }

        // 4. Validaciones de Seguridad y Confianza
        if (!decision.isConclusive) {
            return buildBlocked(decision, "Decisión no conclusiva")
        }

        // Aquí se pueden agregar más validaciones (velocidad, etc) si el SafetyGate no fue suficiente
        // Pero el SafetyGate ya debería haber filtrado esto antes de llegar aquí.

        val result = ActuationResult(
            success = true,
            action = decision.intent,
            reason = "THAMIS decidió ${decision.intent} y Actuator autorizó ejecución",
            cognitiveTraceId = decision.trace.id
        )

        ActuationLogger.log(decision, result)
        return result
    }

    private fun buildBlocked(decision: ThamisDecision, reason: String): ActuationResult {
        val result = ActuationResult(
            success = false,
            action = decision.intent,
            reason = reason,
            cognitiveTraceId = decision.trace.id
        )
        ActuationLogger.log(decision, result)
        return result
    }
}
