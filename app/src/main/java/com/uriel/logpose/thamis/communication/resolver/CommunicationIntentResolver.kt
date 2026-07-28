package com.uriel.logpose.thamis.communication.resolver

import com.uriel.logpose.thamis.communication.model.CommunicationGoal
import com.uriel.logpose.thamis.communication.model.CommunicationIntent
import java.util.*

/**
 * Intérprete de lenguaje natural para el dominio de comunicación.
 */
object CommunicationIntentResolver {

    fun resolve(input: String): CommunicationGoal {
        val query = input.lowercase(Locale.getDefault()).trim()

        return when {
            query.startsWith("llama a") || query.startsWith("llamar a") -> {
                val entity = query.removePrefix("llama a").removePrefix("llamar a").trim()
                CommunicationGoal(intent = CommunicationIntent.CALL_CONTACT, entity = entity, confidence = 0.9f)
            }
            query.contains("mensaje") || query.contains("escribile") || query.contains("decile que") -> {
                val entity = if (query.contains(" a ")) query.substringAfter(" a ").substringBefore(" ").trim() else null
                val text = if (query.contains("que ")) query.substringAfter("que ").trim() else null
                CommunicationGoal(intent = CommunicationIntent.SEND_MESSAGE, entity = entity, freeText = text, confidence = 0.85f)
            }
            query.contains("contestale") || query.contains("responde") || query.contains("respondé") -> {
                CommunicationGoal(intent = CommunicationIntent.REPLY_MESSAGE, confidence = 0.95f)
            }
            query.contains("lee los mensajes") || query.contains("leé los mensajes") -> {
                CommunicationGoal(intent = CommunicationIntent.READ_MESSAGES, confidence = 0.95f)
            }
            query.contains("quien me escribio") || query.contains("quién me escribió") -> {
                CommunicationGoal(intent = CommunicationIntent.WHO_WROTE, confidence = 0.9f)
            }
            query.contains("lee la ultima") || query.contains("leé la última") || query.contains("lee la notificación") -> {
                CommunicationGoal(intent = CommunicationIntent.READ_NOTIFICATION, confidence = 0.9f)
            }
            query.contains("no contestes") || query.contains("ignora") || query.contains("ignorá") -> {
                CommunicationGoal(intent = CommunicationIntent.IGNORE_NOTIFICATION, confidence = 0.95f)
            }
            query.contains("mandale ubicacion") || query.contains("mandá mi ubicación") -> {
                CommunicationGoal(intent = CommunicationIntent.SEND_LOCATION, confidence = 0.9f)
            }
            else -> CommunicationGoal(intent = CommunicationIntent.UNKNOWN, confidence = 0.1f)
        }
    }
}
