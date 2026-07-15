package com.uriel.logpose.logcore.core.intent

/**
 * Describe un patrón de reconocimiento para una intención.
 */
data class IntentPattern(

    val category: IntentCategory,

    val intent: Intent,

    val phrases: List<String>

)