package com.uriel.logpose.domain.nlu

/**
 * Combined result of the NLU engine.
 */
data class NluResult(
    val intent: UserIntent,
    val entities: List<Entity> = emptyList(),
    val confidence: Float = 1.0f
)
