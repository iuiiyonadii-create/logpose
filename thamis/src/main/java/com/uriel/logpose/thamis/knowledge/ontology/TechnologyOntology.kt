package com.uriel.logpose.thamis.knowledge.ontology

/**
 * FASE 27.1 — THAMIS KNOWLEDGE GRAPH
 * FASE 5: TECHNOLOGY ONTOLOGY
 */
object TechnologyOntology {
    val languages = listOf("Kotlin", "Java", "Swift", "TypeScript", "Python")
    val frameworks = listOf("Jetpack Compose", "React", "Ktor", "Spring Boot")
    val databases = listOf("Room", "SQLite", "PostgreSQL", "Firebase")

    fun isKnown(tech: String): Boolean {
        return languages.contains(tech) || frameworks.contains(tech) || databases.contains(tech)
    }
}
