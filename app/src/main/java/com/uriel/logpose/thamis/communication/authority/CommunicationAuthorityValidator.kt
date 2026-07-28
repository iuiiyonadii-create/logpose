package com.uriel.logpose.thamis.communication.authority

import com.uriel.logpose.thamis.communication.model.*
import com.uriel.logpose.thamis.world.model.WorldSnapshot
import com.uriel.logpose.thamis.world.model.communication
import com.uriel.logpose.thamis.world.model.driving

/**
 * Validador final de autoridad para comunicación.
 */
object CommunicationAuthorityValidator {

    fun validate(decision: CommunicationDecision, worldSnapshot: WorldSnapshot): Boolean {
        val speed = worldSnapshot.driving.speedKmh
        
        // Regla: Bloqueo total > 120km/h
        if (speed > 120f) return false
        
        // Regla: Si hay una llamada activa, bloqueamos iniciar otra
        if (worldSnapshot.communication.isCallActive && decision.goal.intent == CommunicationIntent.CALL_CONTACT) {
            return false
        }

        // Regla: Si la confianza es baja (< 0.7), denegar ejecución directa
        if (decision.confidence < 0.7f && decision.decisionType == DecisionType.SHADOW_EXECUTE) {
            return false
        }

        return true
    }
}
