package com.uriel.logpose.thamis.decision

/**
 * Decisión final tomada por THAMIS.
 *
 * THAMIS nunca depende de LogCore.
 */
data class Decision(

    val intent: com.uriel.logpose.thamis.intent.Intent,

    val confidence: Float,

    val requiresConfirmation: Boolean = false

)