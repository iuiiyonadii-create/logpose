package com.thamis.lab.headless.dispatcher

import com.thamis.lab.core.contracts.event.LabEvent
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Dispatches queued events to registered simulation subscribers.
 */
public class DeterministicEventDispatcher {
    private val subscribers = CopyOnWriteArrayList<(LabEvent) -> Unit>()

    public fun subscribe(listener: (LabEvent) -> Unit) {
        subscribers.add(listener)
    }

    public fun unsubscribe(listener: (LabEvent) -> Unit) {
        subscribers.remove(listener)
    }

    public fun dispatch(event: LabEvent) {
        for (subscriber in subscribers) {
            subscriber(event)
        }
    }

    public fun clearSubscribers() {
        subscribers.clear()
    }
}
