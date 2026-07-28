package com.uriel.logpose.thamis.multiagent

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.context.ContextEngine
import com.uriel.logpose.thamis.context.ActivityDetector

/**
 * Agente especialista en seguridad vial para motociclistas.
 * Parte del ecosistema multi-agente de THAMIS LAB.
 */
class LogPoseSafetyAgent : Agent("MotoSafety", "Vial Safety Specialist") {

    override fun execute(task: String): String {
        return "Evaluación de seguridad vial completada."
    }

    /**
     * El voto de este agente es vinculante para acciones de riesgo.
     */
    override fun vote(proposal: String): Boolean {
        val snapshot = ContextEngine.getActiveContext().snapshots.lastOrNull()
        val speed = snapshot?.speedKmh ?: 0f
        
        LogPoseLogger.d("LogPoseSafetyAgent: Votando sobre '$proposal' a ${speed}km/h")

        // Regla de Oro: A más de 120km/h, rechaza cualquier interacción compleja
        if (speed > 120f && proposal.contains("ComplexAction")) {
            LogPoseLogger.w("LogPoseSafetyAgent: VOTO NEGATIVO - Velocidad excesiva para acción compleja.")
            CollaborationBus.postMessage("MotoSafety", "REJECT: Speed too high (${speed}km/h) for complex action.")
            return false
        }

        // Regla de llamadas: Solo si no se está conduciendo (RIDING)
        if (proposal.contains("CALL_CONTACT") && ActivityDetector.getActivity() == ActivityDetector.Activity.RIDING) {
            LogPoseLogger.w("LogPoseSafetyAgent: VOTO NEGATIVO - No se permiten llamadas en movimiento.")
            CollaborationBus.postMessage("MotoSafety", "REJECT: Cannot call while RIDING.")
            return false
        }

        CollaborationBus.postMessage("MotoSafety", "APPROVE: Proposal safe at ${speed}km/h.")
        return true
    }
}
