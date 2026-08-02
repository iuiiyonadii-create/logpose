package com.thamis.lab.headless.queue

import com.thamis.lab.core.contracts.event.LabEvent
import java.util.PriorityQueue

/**
 * Thread-safe Priority Queue ordered deterministically by timestampMs and eventId.
 */
public class DeterministicEventQueue {
    private val comparator = Comparator<LabEvent> { e1, e2 ->
        val timeCompare = e1.timestampMs.compareTo(e2.timestampMs)
        if (timeCompare != 0) timeCompare else e1.eventId.compareTo(e2.eventId)
    }

    private val queue = PriorityQueue(comparator)
    private val lock = Any()

    public fun enqueue(event: LabEvent) {
        synchronized(lock) {
            queue.add(event)
        }
    }

    public fun pollNext(): LabEvent? {
        synchronized(lock) {
            return queue.poll()
        }
    }

    public fun peekNext(): LabEvent? {
        synchronized(lock) {
            return queue.peek()
        }
    }

    public fun size(): Int {
        synchronized(lock) {
            return queue.size
        }
    }

    public fun clear() {
        synchronized(lock) {
            queue.clear()
        }
    }
}
