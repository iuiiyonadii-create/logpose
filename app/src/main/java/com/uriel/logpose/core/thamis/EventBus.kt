package com.uriel.logpose.core.thamis

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Centralized asynchronous message bus for cross-module communication.
 */
class EventBus {
    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    suspend fun emit(event: Event) {
        _events.emit(event)
    }
}
