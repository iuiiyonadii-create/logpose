package com.uriel.logpose.thamis.decision

import com.uriel.logpose.thamis.intent.Intent

/**
 * Posible interpretación de la solicitud.
 */
data class Hypothesis(

    val intent: Intent,

    val score: Float

)