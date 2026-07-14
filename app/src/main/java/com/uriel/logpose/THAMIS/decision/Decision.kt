package com.uriel.logpose.THAMIS.decision

import com.uriel.logpose.THAMIS.intent.Intent

/**
 * Decisión final tomada por THAMIS.
 *
 * THAMIS nunca depende de LogCore.
 */
data class Decision(

    val intent: com.uriel.logpose.THAMIS.intent.Intent,

    val confidence: Float,

    val requiresConfirmation: Boolean = false

)