package com.uriel.logpose.thamis.knowledge

import com.uriel.logpose.thamis.intent.Intent

/**
 * Unidad de conocimiento de THAMIS.
 */
data class KnowledgeRule(

    val intent: Intent,

    val phrases: Set<String>

)