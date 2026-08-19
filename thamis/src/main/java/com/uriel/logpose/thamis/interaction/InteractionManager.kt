package com.uriel.logpose.thamis.interaction

import java.util.concurrent.atomic.AtomicBoolean

/**
 * InteractionManager v3.3: Árbitro absoluto del sistema.
 */
object InteractionManager {

    private val isBusy = AtomicBoolean(false)
    private var lastInteractionTime = 0L
    private val traces = mutableListOf<InteractionTrace>()

    /**
     * Procesa una solicitud de interacción.
     */
    fun process(request: InteractionRequest): InteractionDecision {
        val now = System.currentTimeMillis()
        val effectivePriority = InteractionPolicy.calculateEffectivePriority(request)

        // 1. Validar Cooldown (No hablar demasiado seguido)
        val cooldown = InteractionPolicy.getPostInteractionSilenceMs()
        if (now - lastInteractionTime < cooldown && request.priority.level < InteractionPriority.EMERGENCY.level) {
            recordTrace(request, InteractionDecision.QUEUE, "Cooldown activo")
            InteractionQueue.enqueue(request)
            return InteractionDecision.QUEUE
        }

        // 2. Validar Estado del Sistema
        if (isBusy.get()) {
            val current = InteractionQueue.peek()
            if (current != null && effectivePriority > InteractionPolicy.calculateEffectivePriority(current)) {
                recordTrace(request, InteractionDecision.EXECUTE, "Interrupción por alta prioridad")
                return InteractionDecision.EXECUTE
            }
            recordTrace(request, InteractionDecision.QUEUE, "Sistema ocupado")
            InteractionQueue.enqueue(request)
            return InteractionDecision.QUEUE
        }

        // 3. Ejecución directa si la prioridad es suficiente
        if (effectivePriority > 0) {
            recordTrace(request, InteractionDecision.EXECUTE, "Prioridad válida para ejecución inmediata")
            return InteractionDecision.EXECUTE
        }

        recordTrace(request, InteractionDecision.IGNORE, "Prioridad insuficiente")
        return InteractionDecision.IGNORE
    }

    fun markStarted() {
        isBusy.set(true)
    }

    fun markFinished() {
        isBusy.set(false)
        lastInteractionTime = System.currentTimeMillis()
    }

    private fun recordTrace(req: InteractionRequest, decision: InteractionDecision, reason: String) {
        val trace = InteractionTrace(
            requestId = req.id,
            domain = req.domain,
            decision = decision,
            priority = InteractionPolicy.calculateEffectivePriority(req),
            competingEvents = InteractionQueue.size(),
            reason = reason
        )
        traces.add(trace)
        if (traces.size > 100) traces.removeAt(0)
        InteractionMetrics.record(decision)
    }

    fun getTraces(): List<InteractionTrace> = traces.toList()
}
