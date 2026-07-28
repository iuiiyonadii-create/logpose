package com.uriel.logpose.thamis.cognitive.disambiguation

/**
 * Resultado final del proceso de desambiguación.
 */
data class EntityDecision(
    val selectedEntity: EntityCandidate?,
    val confidence: Float,
    val rejectedCandidates: List<EntityCandidate>,
    val evidences: List<DisambiguationEvidence>,
    val reasoning: String
)
