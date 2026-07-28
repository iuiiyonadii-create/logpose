package com.uriel.logpose.thamis.cognitive.decision

import com.uriel.logpose.thamis.cognitive.model.Risk

/**
 * Define los umbrales de decisión basados en el riesgo.
 */
object DecisionPolicy {

    fun getActionType(confidence: Float, risk: Risk): String {
        return when {
            risk.level < 0.3f -> { // Riesgo bajo
                if (confidence > 0.65f) "EXECUTE" else if (confidence > 0.40f) "CONFIRM" else "IGNORE"
            }
            risk.level < 0.7f -> { // Riesgo medio
                if (confidence > 0.85f) "EXECUTE" else if (confidence > 0.60f) "CONFIRM" else "IGNORE"
            }
            else -> { // Riesgo alto
                if (confidence > 0.95f) "EXECUTE" else if (confidence > 0.75f) "CONFIRM" else "IGNORE"
            }
        }
    }
}
