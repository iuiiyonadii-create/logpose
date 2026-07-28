package com.uriel.logpose.thamis.navigation.model

/**
 * Representa el objetivo específico de navegación dentro del cerebro THAMIS.
 */
data class NavigationGoal(
    val goalType: GoalType,
    val target: String,
    val priority: Float,
    val confidence: Float
) {
    enum class GoalType {
        GO_HOME,
        GO_WORK,
        GO_CONTACT,
        GO_ADDRESS,
        GO_POI,
        CANCEL_ROUTE,
        UNKNOWN
    }
}
