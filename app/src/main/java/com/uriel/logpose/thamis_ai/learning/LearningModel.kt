package com.uriel.logpose.thamis_ai.learning

/**
 * Representation of acquired knowledge.
 */
data class LearningModel(
    val pattern: String,
    val confidence: Float, // 0.0 to 1.0
    val lastUpdated: Long = System.currentTimeMillis()
)
