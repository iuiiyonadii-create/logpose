package com.uriel.logpose.thamis.intent

import com.thamis.lab.core.contracts.intent.Intent

/**
 * Representa un patrón de lenguaje asociado a una intención.
 */
data class IntentPattern(

    val intent: Intent,

    val phrases: Set<String>

)