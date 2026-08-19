package com.uriel.logpose.thamis.communication.safety

import com.uriel.logpose.thamis.communication.model.*

/**
 * Filtro de seguridad para acciones de comunicación.
 */
object CommunicationSafetyGate {

    fun determineDecisionType(
        goal: CommunicationGoal, 
        resolution: ContactResolution?, 
        context: CommunicationContext,
        confidence: Float
    ): Pair<DecisionType, String> {
        
        val speed = context.drivingSpeed

        if (speed > 120f) {
            return DecisionType.REJECT to "Velocidad crítica (>120km/h): Bloqueo total de comunicaciones."
        }

        if (context.isActiveCall && goal.intent == CommunicationIntent.CALL_CONTACT) {
            return DecisionType.WAIT to "Llamada activa: La nueva solicitud debe esperar."
        }

        if (resolution?.isAmbiguous == true) {
            return DecisionType.CONFIRM to "Contacto ambiguo: Se requiere confirmación manual."
        }

        if (goal.intent == CommunicationIntent.SEND_MESSAGE && goal.freeText == null) {
            return DecisionType.CONFIRM to "Mensaje vacío o ambiguo: Se requiere dictado."
        }

        if (resolution != null && resolution.resolvedContact == null && resolution.candidates.isEmpty()) {
            return DecisionType.REJECT to "Contacto no encontrado en la agenda."
        }

        if (speed > 100f) {
            return DecisionType.CONFIRM to "Velocidad alta (>100km/h): Confirmación obligatoria."
        }

        if (confidence < 0.7f) {
            return DecisionType.CONFIRM to "Baja confianza cognitiva: Se requiere validación."
        }

        return DecisionType.SHADOW_EXECUTE to "Acción segura y validada."
    }
}
