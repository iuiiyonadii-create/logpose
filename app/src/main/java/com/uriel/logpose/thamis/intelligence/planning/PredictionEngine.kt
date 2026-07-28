package com.uriel.logpose.thamis.intelligence.planning

/**
 * FASE FINAL — PREDICTION ENGINE
 * Predice tiempos de desarrollo y posibles cuellos de botella basándose en la complejidad detectada.
 */
object PredictionEngine {

    fun predictTimeline(tasks: List<String>): Int {
        // Retorna horas estimadas
        return tasks.size * 2
    }

    fun detectBottlenecks(plan: Map<String, List<String>>): List<String> {
        return plan.filter { it.value.size > 3 }.keys.toList()
    }
}
