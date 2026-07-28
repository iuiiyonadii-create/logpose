package com.uriel.logpose.thamis.communication.validation

import com.uriel.logpose.thamis.communication.model.CommunicationContext
import com.uriel.logpose.thamis.communication.shadow.CommunicationShadowController

/**
 * Suite de pruebas de estrés para el dominio de comunicación.
 */
class CommunicationStressTest {

    fun run() {
        val scenarios = listOf(
            "Llamá a Juan" to CommunicationContext(drivingSpeed = 60f), // Dos Juan -> CONFIRM
            "Mandale mensaje a Mama" to CommunicationContext(drivingSpeed = 40f), // Un solo Mama -> SHADOW_EXECUTE
            "Contestale" to CommunicationContext(recentConversation = "Mama"), // Sin conversación activa en context -> REJECT
            "Leé los mensajes" to CommunicationContext(drivingSpeed = 80f), // SHADOW_EXECUTE
            "Mandale ubicacion" to CommunicationContext(drivingSpeed = 20f), // SHADOW_EXECUTE
            "Llamá a desconocido" to CommunicationContext(drivingSpeed = 50f), // Sin contacto -> REJECT
            "Llamá a Mama" to CommunicationContext(drivingSpeed = 125f), // > 120 km/h -> REJECT
            "Mandale mensaje a Juan" to CommunicationContext(drivingSpeed = 110f) // 100-120 km/h -> CONFIRM
        )

        scenarios.forEach { (input, context) ->
            CommunicationShadowController.process(input, context)
        }
    }
}
