package com.uriel.logpose.thamis.communication.shadow

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.communication.audit.CommunicationAudit
import com.uriel.logpose.thamis.communication.audit.CommunicationTrace
import com.uriel.logpose.thamis.communication.evaluator.CommunicationEvidenceEvaluator
import com.uriel.logpose.thamis.communication.model.CommunicationContext
import com.uriel.logpose.thamis.communication.model.CommunicationDecision
import com.uriel.logpose.thamis.communication.resolver.CommunicationEntityResolver
import com.uriel.logpose.thamis.communication.resolver.CommunicationIntentResolver
import com.uriel.logpose.thamis.communication.safety.CommunicationSafetyGate

/**
 * Orquestador del dominio COMMUNICATION en modo Shadow.
 */
object CommunicationShadowController {

    fun process(input: String, context: CommunicationContext) {
        val startTime = System.currentTimeMillis()

        // 1. Interpretar Intención
        val goal = CommunicationIntentResolver.resolve(input)

        // 2. Resolver Entidad (Contacto/Grupo)
        val resolution = CommunicationEntityResolver.resolve(goal.entity)

        // 3. Evaluar Evidencias y Confianza
        val (finalConfidence, evidences) = CommunicationEvidenceEvaluator.evaluate(goal, resolution, context)

        // 4. Determinar Decisión de Seguridad
        val (decisionType, reason) = CommunicationSafetyGate.determineDecisionType(goal, resolution, context, finalConfidence)

        val decision = CommunicationDecision(
            goal = goal,
            confidence = finalConfidence,
            reason = reason,
            evidence = evidences,
            risk = if (context.drivingSpeed > 100) "HIGH" else "LOW",
            decisionType = decisionType
        )

        val latency = System.currentTimeMillis() - startTime

        // 5. Auditar
        CommunicationAudit.record(CommunicationTrace(input, goal, decision, latency))

        // 6. Logcat
        LogPoseLogger.i("[THAMIS_COMMUNICATION] INPUT: '$input' | GOAL: ${goal.intent} | ENTITY: ${goal.entity ?: "NONE"} | CONFIDENCE: $finalConfidence | DECISION: $decisionType | REASON: $reason")
    }
}
