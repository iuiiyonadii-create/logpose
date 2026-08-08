package com.uriel.logpose.thamis.decision

import com.thamis.lab.core.contracts.intent.Intent

/**
 * Decisión final tomada por THAMIS.
 */
data class Decision(

    val intent: Intent,

    val confidence: Float,

    val entities: Map<String, String> = emptyMap(),

    val requiresConfirmation: Boolean = false

)