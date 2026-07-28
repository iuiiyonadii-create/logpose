package com.uriel.logpose.thamis.feedback.collector

import com.uriel.logpose.thamis.feedback.model.UserFeedbackEvent
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Repositorio central para la recopilación de experiencias de usuario.
 */
object FeedbackCollector {
    private val events = CopyOnWriteArrayList<UserFeedbackEvent>()

    fun record(event: UserFeedbackEvent) {
        events.add(event)
        if (events.size > 1000) events.removeAt(0)
    }

    fun getAll(): List<UserFeedbackEvent> = events.toList()

    fun clear() = events.clear()
}
