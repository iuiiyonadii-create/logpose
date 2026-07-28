package com.uriel.logpose.thamis_ai.context

/**
 * Handles time-based context (sequence of events, duration).
 */
data class TemporalContext(
    val lastEventTimestamp: Long = 0,
    val drivingDuration: Long = 0
)
