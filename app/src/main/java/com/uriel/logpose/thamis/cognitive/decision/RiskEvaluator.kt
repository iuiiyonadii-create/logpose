package com.uriel.logpose.thamis.cognitive.decision

import com.uriel.logpose.thamis.cognitive.model.Goal
import com.uriel.logpose.thamis.cognitive.model.Risk

/**
 * Calcula el riesgo asociado a un objetivo específico.
 */
object RiskEvaluator {

    fun evaluate(goal: Goal): Risk {
        return when (goal.category) {
            Goal.Category.MULTIMEDIA -> {
                if (goal.targetState.contains("volume")) {
                    Risk(Risk.Type.SYSTEM, 0.1f, Risk.Strategy.SILENT_EXECUTION)
                } else {
                    Risk(Risk.Type.SYSTEM, 0.2f, Risk.Strategy.SILENT_EXECUTION)
                }
            }
            Goal.Category.NAVIGATION -> {
                // El riesgo de navegación es medio (0.5)
                Risk(Risk.Type.PHYSICAL, 0.5f, Risk.Strategy.REQUEST_CONFIRMATION)
            }
            Goal.Category.COMMUNICATION -> {
                Risk(Risk.Type.SOCIAL, 0.9f, Risk.Strategy.REQUEST_CONFIRMATION)
            }
            else -> {
                Risk(Risk.Type.SYSTEM, 0.5f, Risk.Strategy.REQUEST_CONFIRMATION)
            }
        }
    }
}
