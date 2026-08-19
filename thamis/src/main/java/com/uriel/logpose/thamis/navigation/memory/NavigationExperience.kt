package com.uriel.logpose.thamis.navigation.memory

import com.uriel.logpose.thamis.navigation.model.NavigationGoal

/**
 * Representa una unidad de experiencia de navegación guardada en la memoria episódica.
 */
data class NavigationExperience(
    val destination: String,
    val goalType: NavigationGoal.GoalType,
    val timestamp: Long = System.currentTimeMillis(),
    val speedKmh: Int,
    val decision: String,
    val success: Boolean,
    val userCorrection: Boolean = false
)
