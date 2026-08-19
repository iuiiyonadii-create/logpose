package com.uriel.logpose.thamis.calibration

/**
 * Representa una sugerencia de mejora para el motor cognitivo basada en datos reales.
 */
data class CalibrationRecommendation(
    val type: Type,
    val description: String,
    val reason: String
) {
    enum class Type {
        INCREASE_CONFIDENCE,
        DECREASE_CONFIDENCE,
        ADD_ENTITY,
        ADD_PHONETIC_RULE,
        IMPROVE_CONTEXT,
        NO_ACTION
    }
}
