package com.uriel.logpose.features.voice

import java.util.concurrent.atomic.AtomicLong

/**
 * SlotDebouncer: Evita la ejecución duplicada de un mismo slot en un corto periodo.
 */
class SlotDebouncer(private val windowMs: Long = 1000L) {
    private val lastExecuted = AtomicLong(0L)
    private val lastSlot = AtomicLong(-1L)

    fun shouldExecute(slotId: Int): Boolean {
        val now = System.currentTimeMillis()
        val last = lastExecuted.get()
        val sameSlot = lastSlot.get() == slotId.toLong()

        if (sameSlot && (now - last) < windowMs) {
            return false
        }

        lastExecuted.set(now)
        lastSlot.set(slotId.toLong())
        return true
    }
}
