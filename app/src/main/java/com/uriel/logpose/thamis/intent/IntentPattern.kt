package com.uriel.logpose.thamis.intent

/**
 * Representa un patrón de lenguaje asociado a una intención.
 */
data class IntentPattern(

    val intent: Intent,

    val phrases: Set<String>

)