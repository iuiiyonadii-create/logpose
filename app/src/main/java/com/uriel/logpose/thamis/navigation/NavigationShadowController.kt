package com.uriel.logpose.thamis.navigation

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.navigation.model.*
import com.uriel.logpose.thamis.navigation.audit.NavigationTrace
import com.uriel.logpose.thamis.navigation.confidence.ConfidenceDecayEngine
import com.uriel.logpose.thamis.navigation.confidence.DestinationConfidence
import com.uriel.logpose.thamis.navigation.memory.NavigationMemory
import java.util.UUID

/**
 * Orquestador del modo sombra para el dominio de navegación.
 * Intelligence v1.0: Registro de trazas detalladas con memoria y decaimiento.
 */
object NavigationShadowController {
    private const val TAG = "THAMIS_NAVIGATION"

    fun observe(text: String, context: NavigationContext, isCallActive: Boolean = false) {
        // 1. Clasificar y Resolver Objetivo
        val intentCategory = RouteIntentClassifier.classify(text)
        val goalType = DestinationResolver.resolve(text)
        
        // 2. Calcular Confianza con Memoria
        val baseConf = DestinationConfidence.getBaseConfidence(goalType)
        val memoryBoost = NavigationMemory.getMemoryBoost(text)
        val initialConfidence = (baseConf + memoryBoost).coerceAtMost(1.0f)

        val goal = NavigationGoal(
            goalType = goalType,
            target = text,
            priority = 0.8f,
            confidence = initialConfidence
        )

        // 3. Evaluar Prioridad
        val isWaiting = isCallActive || NavigationPriorityGuard.shouldWait(com.uriel.logpose.thamis.cognitive.model.WorldState(
            driving = com.uriel.logpose.thamis.cognitive.model.WorldState.DrivingState(speedKmh = context.speedKmh),
            system = com.uriel.logpose.thamis.cognitive.model.WorldState.SystemState(activeCall = isCallActive),
            external = com.uriel.logpose.thamis.cognitive.model.WorldState.ExternalState()
        ))

        // 4. Crear Decisión
        val decision = NavigationDecision(
            goal = goal,
            destination = goal.target,
            confidence = goal.confidence,
            risk = 0.5f,
            requiresConfirmation = goal.confidence < 0.90f || context.speedKmh > 100,
            reasoning = if (isWaiting) "WAITING_FOR_PRIORITY_RELEASE" else "Intelligence Shadow Analysis"
        )

        // 5. Simular Decaimiento (para la traza)
        val elapsedSimulated = 5000L // 5 segundos después
        val decayedConf = ConfidenceDecayEngine.calculateDecay(decision.confidence, elapsedSimulated)

        // 6. Generar Traza Completa
        val trace = NavigationTrace(
            id = UUID.randomUUID().toString(),
            input = text,
            goal = goal.goalType.name,
            intentCategory = intentCategory.name,
            destination = decision.destination,
            destinationConfidence = baseConf,
            memoryBoost = memoryBoost,
            confidenceDecay = decision.confidence - decayedConf,
            finalConfidence = decayedConf,
            expired = ConfidenceDecayEngine.isExpired(elapsedSimulated),
            learningContribution = if (memoryBoost > 0) "HABITUAL_DESTINATION" else null,
            evidences = listOf("GPS_AVAILABLE", "KNOWN_DESTINATION", "DRIVING_CONTEXT"),
            confidence = decision.confidence,
            speedKmh = context.speedKmh,
            gpsAvailable = context.gpsAvailable,
            decision = if (isWaiting) "WAIT" else "SHADOW_EXECUTE"
        )

        // 7. Loguear Auditoría
        LogPoseLogger.i("[$TAG] INPUT: \"$text\"")
        LogPoseLogger.i("[$TAG] GOAL: ${goal.goalType}")
        LogPoseLogger.i("[$TAG] DESTINATION: ${decision.destination}")
        LogPoseLogger.i("[$TAG] CONFIDENCE: ${decision.confidence} (MemoryBoost: +$memoryBoost)")
        LogPoseLogger.i("[$TAG] DECISION: ${trace.decision}")
    }
}
