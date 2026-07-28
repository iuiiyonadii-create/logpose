package com.uriel.logpose.thamis.communication.evaluator

import com.uriel.logpose.thamis.communication.model.CommunicationContext
import com.uriel.logpose.thamis.communication.model.CommunicationGoal
import com.uriel.logpose.thamis.communication.model.ContactResolution

/**
 * Evalúa las evidencias para determinar la confianza en una decisión de comunicación.
 */
object CommunicationEvidenceEvaluator {

    fun evaluate(goal: CommunicationGoal, resolution: ContactResolution?, context: CommunicationContext): Pair<Float, List<String>> {
        var confidence = goal.confidence
        val evidences = mutableListOf<String>()

        if (resolution?.resolvedContact != null) {
            confidence += 0.35f
            evidences.add("CONTACT_FOUND (+0.35)")
        } else if (resolution != null && resolution.candidates.isEmpty()) {
            confidence -= 0.60f
            evidences.add("NO_CONTACT (-0.60)")
        }

        if (context.recentConversation == goal.entity) {
            confidence += 0.20f
            evidences.add("RECENT_CONVERSATION (+0.20)")
        }

        if (context.notificationSource != null) {
            confidence += 0.25f
            evidences.add("ACTIVE_NOTIFICATION (+0.25)")
        }

        if (context.isActiveCall) {
            confidence -= 0.50f
            evidences.add("ACTIVE_CALL (-0.50)")
        }

        if (context.drivingSpeed > 100f) {
            confidence -= 0.20f
            evidences.add("HIGH_SPEED (-0.20)")
        }

        return confidence.coerceIn(0f, 1f) to evidences
    }
}
