package com.uriel.logpose.thamis.integration.dependency

/**
 * Mapa de dependencias funcionales entre los dominios cognitivos.
 */
object DependencyGraph {
    
    private val dependencies = mapOf(
        "NAVIGATION" to listOf("WORLD_MODEL", "PLANNING", "SAFETY", "AUTHORITY"),
        "COMMUNICATION" to listOf("WORLD_MODEL", "PLANNING", "SAFETY", "DIALOG"),
        "MULTIMEDIA" to listOf("WORLD_MODEL", "PLANNING"),
        "DIALOG" to listOf("WORLD_MODEL")
    )

    fun getDependenciesFor(domain: String): List<String> {
        return dependencies[domain] ?: emptyList()
    }
}
