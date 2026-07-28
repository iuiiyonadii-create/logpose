package com.uriel.logpose.thamis_ai.memory

import java.util.concurrent.TimeUnit

/**
 * Defines retention policies and access rules for different memory types.
 */
object MemoryPolicy {
    
    val TEMPORARY_TTL = TimeUnit.HOURS.toMillis(1)
    val HABIT_VALIDATION_THRESHOLD = 5 // Times an action must repeat to be a habit
    
    fun getExpiration(type: MemoryType): Long? {
        return when (type) {
            MemoryType.TEMPORARY -> System.currentTimeMillis() + TEMPORARY_TTL
            else -> null // Preferences and Habits are permanent unless cleared
        }
    }
}
