package com.uriel.logpose.thamis.knowledge.reasoning

/**
 * FASE 27.1 — THAMIS KNOWLEDGE GRAPH
 * FASE 12: RECOMMENDATION ENGINE
 */
object RecommendationEngine {

    fun suggestSolutions(problem: String): List<String> {
        return when {
            problem.contains("Bluetooth") -> listOf("BluetoothManager", "Headset Profile")
            problem.contains("Storage") -> listOf("Room", "DataStore")
            else -> listOf("Generic Solution")
        }
    }
}
