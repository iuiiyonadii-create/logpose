package com.uriel.logpose.thamis.integration.dispatcher

import com.uriel.logpose.thamis.integration.model.EventType
import com.uriel.logpose.thamis.integration.model.IntegrationEvent

/**
 * Bus de eventos unificado para notificar cambios de estado entre dominios.
 */
object EventBus {
    private val listeners = mutableListOf<(IntegrationEvent) -> Unit>()

    fun subscribe(listener: (IntegrationEvent) -> Unit) {
        listeners.add(listener)
    }

    fun publish(type: EventType, source: String, payload: Any? = null) {
        val event = IntegrationEvent(type = type, source = source, payload = payload)
        listeners.forEach { it(event) }
    }
}
