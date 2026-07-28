package com.uriel.logpose.thamis.context

/**
 * FASE 25.18 — THAMIS ADVANCED CONTEXT INTELLIGENCE
 * FASE 4: TEMPORAL CONTEXT
 */
object TemporalContext {
    private var lastEventTime: Long = 0
    private var lastIntent: String? = null

    fun recordEvent(intent: String) {
        lastEventTime = System.currentTimeMillis()
        lastIntent = intent
    }

    fun getMsSinceLastEvent(): Long = System.currentTimeMillis() - lastEventTime

    fun isRecent(intent: String, windowMs: Long): Boolean {
        return lastIntent == intent && getMsSinceLastEvent() < windowMs
    }
}
