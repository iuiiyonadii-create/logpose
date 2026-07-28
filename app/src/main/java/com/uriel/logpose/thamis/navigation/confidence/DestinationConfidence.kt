package com.uriel.logpose.thamis.navigation.confidence

import com.uriel.logpose.thamis.navigation.model.NavigationGoal

/**
 * Define niveles de confiabilidad intrínsecos para diferentes tipos de destinos.
 */
object DestinationConfidence {

    private val CONFIDENCE_MAP = mapOf(
        NavigationGoal.GoalType.GO_HOME to 1.00f,
        NavigationGoal.GoalType.GO_WORK to 0.98f,
        NavigationGoal.GoalType.GO_POI to 0.70f,
        NavigationGoal.GoalType.GO_ADDRESS to 0.60f,
        NavigationGoal.GoalType.GO_CONTACT to 0.45f,
        NavigationGoal.GoalType.CANCEL_ROUTE to 0.95f
    )

    /**
     * Devuelve el factor de confianza base para un tipo de destino.
     */
    fun getBaseConfidence(goalType: NavigationGoal.GoalType): Float {
        return CONFIDENCE_MAP[goalType] ?: 0.5f
    }
}
