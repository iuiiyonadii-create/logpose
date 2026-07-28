package com.uriel.logpose.thamis.interaction

import java.util.concurrent.PriorityBlockingQueue

/**
 * Cola de interacciones con lógica de ordenamiento dinámico.
 */
object InteractionQueue {

    private val queue = PriorityBlockingQueue<InteractionRequest>(10, 
        compareByDescending<InteractionRequest> { InteractionPolicy.calculateEffectivePriority(it) }
        .thenBy { it.timestamp }
    )

    fun enqueue(request: InteractionRequest) {
        // Lógica de Fusión (MERGE)
        val similar = queue.find { it.domain == request.domain && it.priority == request.priority }
        if (similar != null && canMerge(similar, request)) {
            mergeRequests(similar, request)
        } else {
            queue.put(request)
        }
    }

    fun poll(): InteractionRequest? {
        cleanExpired()
        return queue.poll()
    }

    fun peek(): InteractionRequest? = queue.peek()

    private fun canMerge(old: InteractionRequest, new: InteractionRequest): Boolean {
        // Fusionamos si son mensajes del mismo dominio y han pasado menos de 5 segundos
        return old.domain == InteractionRequest.Domain.NOTIFICATION && 
               (System.currentTimeMillis() - old.timestamp < 5000)
    }

    private fun mergeRequests(old: InteractionRequest, new: InteractionRequest) {
        // Implementación simplificada: removemos el viejo e insertamos una versión agrupada
        queue.remove(old)
        val mergedPayload = "Múltiples notificaciones de ${old.domain}"
        queue.put(old.copy(payload = mergedPayload, timestamp = System.currentTimeMillis()))
    }

    private fun cleanExpired() {
        queue.removeIf { it.isExpired() }
    }

    fun clear() = queue.clear()
    fun size() = queue.size
}
