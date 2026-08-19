package com.uriel.logpose.thamis.orchestration.queue

import com.uriel.logpose.thamis.orchestration.model.PendingAction
import java.util.concurrent.PriorityBlockingQueue

/**
 * Gestor de la cola de prioridades dinámica.
 */
object PriorityQueueManager {
    
    private val queue = PriorityBlockingQueue<PendingAction>(11, compareByDescending { it.priority })

    fun enqueue(action: PendingAction) {
        // Evitar duplicados exactos
        if (queue.any { it.intent == action.intent && it.domain == action.domain }) return
        
        queue.add(action)
    }

    fun poll(): PendingAction? {
        val action = queue.poll()
        if (action?.isExpired() == true) return poll()
        return action
    }

    fun peek(): PendingAction? = queue.peek()

    fun clear() = queue.clear()

    fun getQueuedActions(): List<PendingAction> = queue.toList()
    
    fun removeIf(predicate: (PendingAction) -> Boolean) = queue.removeIf(predicate)
}
