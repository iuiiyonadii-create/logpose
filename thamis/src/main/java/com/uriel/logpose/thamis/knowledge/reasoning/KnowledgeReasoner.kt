package com.uriel.logpose.thamis.knowledge.reasoning

/**
 * FASE 27.1 — THAMIS KNOWLEDGE GRAPH
 * FASE 10: KNOWLEDGE REASONER
 */
object KnowledgeReasoner {

    /**
     * Infiere tecnologías recomendadas basadas en requisitos.
     */
    fun inferRecommendations(requirements: List<String>): List<String> {
        val recommendations = mutableListOf<String>()
        if (requirements.contains("Android") && requirements.contains("Offline")) {
            recommendations.add("Room Database")
        }
        if (requirements.contains("UI") && requirements.contains("Android")) {
            recommendations.add("Jetpack Compose")
        }
        return recommendations
    }
}
