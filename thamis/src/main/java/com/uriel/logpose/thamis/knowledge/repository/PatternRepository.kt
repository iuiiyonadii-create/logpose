package com.uriel.logpose.thamis.knowledge.repository

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 27.1 — THAMIS KNOWLEDGE GRAPH
 * FASE 9: PATTERN REPOSITORY
 */
object PatternRepository {
    private val patterns = mutableMapOf<String, String>()

    fun registerPattern(name: String, structure: String) {
        patterns[name] = structure
        LogPoseLogger.i("PatternRepository: Patrón registrado: $name")
    }

    fun getPattern(name: String): String? = patterns[name]
}
