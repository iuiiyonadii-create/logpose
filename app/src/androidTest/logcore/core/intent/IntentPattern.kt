package com.uriel.logpose.logcore.core.intent

import com.thamis.lab.core.contracts.intent.Intent

/**
 * Describe un patrón de reconocimiento para una intención.
 */
data class IntentPattern(

    val category: IntentCategory,

    val intent: Intent,

    val phrases: List<String>

)