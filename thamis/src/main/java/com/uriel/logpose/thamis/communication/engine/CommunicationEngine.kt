package com.uriel.logpose.thamis.communication.engine

import com.uriel.logpose.thamis.communication.model.*
import com.uriel.logpose.thamis.communication.resolver.CommunicationEntityResolver
import com.uriel.logpose.thamis.communication.evaluator.CommunicationEvidenceEvaluator
import com.uriel.logpose.thamis.communication.safety.CommunicationSafetyGate
import com.uriel.logpose.thamis.world.context.WorldContextEngine
import com.uriel.logpose.thamis.world.model.navigation

/**
 * CommunicationEngine v1.0: Orquestador del dominio de comunicación.
 */
object CommunicationEngine {

    fun process(goal: CommunicationGoal): CommunicationDecision {
        val worldSnapshot = WorldContextEngine.getCurrentState()
        
        // 1. Resolución de Contacto si aplica
        var resolution: ContactResolution? = null
        if (goal.intent == CommunicationIntent.CALL_CONTACT || goal.intent == CommunicationIntent.SEND_MESSAGE) {
            resolution = CommunicationEntityResolver.resolve(goal.entity)
        }

        // 2. Mock Communication Context for calculation
        val commContext = CommunicationContext(
            isActiveCall = worldSnapshot.systems.communication.isCallActive,
            drivingSpeed = worldSnapshot.vehicle.speedKmh,
            gpsState = worldSnapshot.navigation.gpsStatus.name
        )

        // 3. Evaluar Evidencias y Confianza
        val (finalConfidence, evidences) = CommunicationEvidenceEvaluator.evaluate(goal, resolution, commContext)

        // 4. Análisis de Seguridad
        val (decisionType, reason) = CommunicationSafetyGate.determineDecisionType(goal, resolution, commContext, finalConfidence)

        return CommunicationDecision(
            goal = goal,
            confidence = finalConfidence,
            reason = reason,
            evidence = evidences,
            risk = if (worldSnapshot.vehicle.speedKmh > 100) "HIGH" else "LOW",
            decisionType = decisionType
        )
    }
}
