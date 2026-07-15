package com.uriel.logpose.thamis.hypothesis

import com.uriel.logpose.thamis.evidence.Evidence
import com.uriel.logpose.thamis.intent.Intent

/**
 * Builder de hipótesis.
 */
object HypothesisBuilder {

    fun build(

        intent: Intent,

        evidences: List<Evidence>,

        confidence: Float,

        explanation: String

    ): Hypothesis {

        return Hypothesis(

            intent = intent,

            evidences = evidences,

            score = HypothesisScore(

                confidence = confidence,

                explanation = explanation

            )

        )

    }

}