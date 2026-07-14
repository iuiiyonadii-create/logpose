package com.uriel.logpose.thamis.decision

import com.uriel.logpose.thamis.intent.Intent

/**
 * Posible interpretación construida por THAMIS.
 */
data class Hypothesis(

    val intent: Intent,

    val score: Float,

    val evidence: DecisionEvidence

)