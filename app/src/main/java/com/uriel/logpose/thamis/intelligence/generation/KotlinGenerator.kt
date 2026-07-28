package com.uriel.logpose.thamis.intelligence.generation

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE FINAL — CODE GENERATION
 * Generador especializado en estructuras puro Kotlin.
 */
object KotlinGenerator {

    fun generateDataClass(name: String, properties: Map<String, String>): String {
        val props = properties.entries.joinToString(",\n    ") { "val ${it.key}: ${it.value}" }
        return "data class $name(\n    $props\n)"
    }

    fun generateRepositoryInterface(name: String): String {
        return """
            interface ${name}Repository {
                fun get(): List<String>
                fun save(item: String)
            }
        """.trimIndent()
    }
}
