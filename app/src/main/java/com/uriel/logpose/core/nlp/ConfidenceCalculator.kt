package com.uriel.logpose.core.nlp

object ConfidenceCalculator {

    fun calculate(

        intent: Intent

    ): Float {

        return if (
            intent == Intent.UNKNOWN
        ) {

            0f

        } else {

            1f
        }
    }
}