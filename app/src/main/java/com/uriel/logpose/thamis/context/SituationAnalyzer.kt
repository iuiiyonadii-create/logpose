package com.uriel.logpose.thamis.context

import com.uriel.logpose.thamis.voiceexperience.model.DrivingContext

/**
 * FASE 25.18 — THAMIS ADVANCED CONTEXT INTELLIGENCE
 * FASE 3: SITUATION ANALYZER
 */
object SituationAnalyzer {

    enum class Situation {
        IDLE,
        DRIVING_NORMAL,
        DRIVING_HIGH_LOAD,
        STATIONARY_ACTIVE,
        CALL_IN_PROGRESS
    }

    /**
     * Transforma datos crudos de contexto en una situación entendible.
     */
    fun analyzeSituation(context: DrivingContext): Situation {
        return when {
            context.isCallActive -> Situation.CALL_IN_PROGRESS
            context.cognitiveLoad > 0.7f -> Situation.DRIVING_HIGH_LOAD
            context.speedKmh > 20f -> Situation.DRIVING_NORMAL
            context.speedKmh > 0f -> Situation.STATIONARY_ACTIVE
            else -> Situation.IDLE
        }
    }
}
