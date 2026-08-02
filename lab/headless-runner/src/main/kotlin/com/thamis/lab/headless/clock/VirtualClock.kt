package com.thamis.lab.headless.clock

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * Virtual Clock for controlling simulation time deterministically.
 */
public class VirtualClock(initialTimeMs: Long = 0L) {
    private val timeMs = AtomicLong(initialTimeMs)
    private val speedMultiplier = AtomicLong(java.lang.Double.doubleToLongBits(1.0))
    private val isPausedState = AtomicBoolean(false)

    public val currentTimeMs: Long get() = timeMs.get()
    public val isPaused: Boolean get() = isPausedState.get()
    public val timeMultiplier: Double get() = java.lang.Double.longBitsToDouble(speedMultiplier.get())

    public fun advanceTimeBy(deltaMs: Long): Long {
        if (isPausedState.get() || deltaMs <= 0) return timeMs.get()
        val effectiveDelta = (deltaMs * timeMultiplier).toLong()
        return timeMs.addAndGet(effectiveDelta)
    }

    public fun setTime(newTimeMs: Long) {
        timeMs.set(newTimeMs)
    }

    public fun setSpeedMultiplier(multiplier: Double) {
        require(multiplier > 0.0) { "Speed multiplier must be positive" }
        speedMultiplier.set(java.lang.Double.doubleToLongBits(multiplier))
    }

    public fun pause() {
        isPausedState.set(true)
    }

    public fun resume() {
        isPausedState.set(false)
    }

    public fun reset(startTimeMs: Long = 0L) {
        timeMs.set(startTimeMs)
        speedMultiplier.set(java.lang.Double.doubleToLongBits(1.0))
        isPausedState.set(false)
    }
}
