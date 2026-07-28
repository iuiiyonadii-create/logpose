package com.uriel.logpose.thamis.knowledge.ontology

/**
 * FASE 27.1 — THAMIS KNOWLEDGE GRAPH
 * FASE 6: ARCHITECTURE ONTOLOGY
 */
object ArchitectureOntology {
    val patterns = listOf(
        "Clean Architecture",
        "MVVM",
        "MVI",
        "DDD",
        "Hexagonal",
        "Microservices"
    )

    fun getRecommendedLayers(architecture: String): List<String> {
        return when (architecture) {
            "Clean Architecture" -> listOf("Presentation", "Domain", "Data")
            "Hexagonal" -> listOf("Infrastructure", "Application", "Domain")
            else -> emptyList()
        }
    }
}
