package com.uriel.logpose.thamis.communication.model

import java.util.*

/**
 * Intenciones específicas del dominio de comunicación THAMIS v1.0.
 */
enum class CommunicationIntent {
    CALL_CONTACT,
    SEND_MESSAGE,
    READ_MESSAGES,
    REPLY_MESSAGE,
    READ_NOTIFICATION,
    IGNORE_NOTIFICATION,
    SEND_LOCATION,
    WHO_WROTE,
    UNKNOWN
}

/**
 * Objetivo cognitivo para el dominio de comunicación.
 */
data class CommunicationGoal(
    val id: String = UUID.randomUUID().toString(),
    val intent: CommunicationIntent,
    val entity: String? = null,
    val freeText: String? = null,
    val confidence: Float = 0f
)

/**
 * Resultado de la resolución de un contacto.
 */
data class ContactResolution(
    val originalName: String,
    val candidates: List<ContactCandidate>,
    val resolvedContact: ContactCandidate? = null,
    val isAmbiguous: Boolean = false
)

data class ContactCandidate(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val alias: List<String> = emptyList(),
    val priority: Int = 0,
    val isFavorite: Boolean = false
)

/**
 * Contexto actual para la toma de decisiones de comunicación.
 */
data class CommunicationContext(
    val isActiveCall: Boolean = false,
    val drivingSpeed: Float = 0f,
    val notificationSource: String? = null,
    val recentConversation: String? = null,
    val bluetoothConnected: Boolean = false,
    val gpsState: String = "UNKNOWN",
    val currentMusic: String? = null,
    val currentNavigation: String? = null
)

/**
 * Decisión final del motor de comunicación.
 */
data class CommunicationDecision(
    val goal: CommunicationGoal,
    val confidence: Float,
    val reason: String,
    val evidence: List<String>,
    val risk: String,
    val decisionType: DecisionType
)

enum class DecisionType {
    SHADOW_EXECUTE,
    CONFIRM,
    REJECT,
    WAIT,
    IGNORE
}
