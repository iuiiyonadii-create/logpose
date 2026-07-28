package com.uriel.logpose.thamis.knowledge.ontology

/**
 * FASE 27.1 — THAMIS KNOWLEDGE GRAPH
 * FASE 7: DESIGN PATTERN ONTOLOGY
 */
object PatternOntology {
    val commonPatterns = listOf(
        "Repository",
        "Factory",
        "Observer",
        "Strategy",
        "Command",
        "Adapter",
        "Decorator",
        "Facade",
        "Singleton"
    )

    fun getDescription(pattern: String): String? {
        return when (pattern) {
            "Repository" -> "Abstrae el acceso a datos."
            "Observer" -> "Define una dependencia uno-a-muchos entre objetos."
            else -> null
        }
    }
}
