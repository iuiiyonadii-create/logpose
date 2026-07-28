package com.uriel.logpose.thamis.integration.priority

import com.uriel.logpose.thamis.integration.model.GlobalPriority

/**
 * Motor de prioridades globales para la toma de decisiones unificada.
 */
object GlobalPriorityEngine {
    
    /**
     * Orden oficial de prioridades.
     */
    private val hierarchy = listOf(
        GlobalPriority.EMERGENCY,
        GlobalPriority.SAFETY,
        GlobalPriority.CALL,
        GlobalPriority.NAVIGATION,
        GlobalPriority.DIALOG,
        GlobalPriority.COMMUNICATION,
        GlobalPriority.MULTIMEDIA,
        GlobalPriority.NOTIFICATIONS,
        GlobalPriority.BACKGROUND
    )

    fun getPriorityLevel(priority: GlobalPriority): Int = priority.level

    fun isHigherThan(p1: GlobalPriority, p2: GlobalPriority): Boolean {
        return p1.level > p2.level
    }
}
