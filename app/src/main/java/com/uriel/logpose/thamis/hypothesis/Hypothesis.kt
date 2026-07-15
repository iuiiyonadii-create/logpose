package com.uriel.logpose.thamis.hypothesis

import com.uriel.logpose.thamis.evidence.Evidence
import com.uriel.logpose.thamis.intent.Intent
import java.util.UUID

/**
 * Posible interpretación de las evidencias.
 */
data class Hypothesis(

    val id: UUID = UUID.randomUUID(),

    val intent: Intent,

    val evidences: List<Evidence>,

    val score: HypothesisScore

)