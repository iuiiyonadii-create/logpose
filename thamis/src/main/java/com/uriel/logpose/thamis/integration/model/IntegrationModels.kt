package com.uriel.logpose.thamis.integration.model

import java.util.*

/**
 * Resultados de una etapa del pipeline de integración.
 */
enum class PipelineResult {
    SUCCESS,
    REJECT,
    RETRY,
    CANCEL,
    WAIT,
    FAILURE
}

/**
 * Niveles de prioridad global para el cerebro THAMIS.
 */
enum class GlobalPriority(val level: Int) {
    EMERGENCY(1000),
    SAFETY(900),
    CALL(800),
    NAVIGATION(700),
    DIALOG(600),
    COMMUNICATION(500),
    MULTIMEDIA(400),
    NOTIFICATIONS(300),
    BACKGROUND(100)
}

/**
 * Registro de un evento en el EventBus.
 */
data class IntegrationEvent(
    val id: String = UUID.randomUUID().toString(),
    val type: EventType,
    val source: String,
    val payload: Any? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class EventType {
    IntentDetected,
    PlanCreated,
    PlanRejected,
    NavigationStarted,
    MusicStarted,
    ConversationStarted,
    ConversationFinished,
    CallStarted,
    NotificationReceived,
    JourneyStarted,
    JourneyFinished
}
