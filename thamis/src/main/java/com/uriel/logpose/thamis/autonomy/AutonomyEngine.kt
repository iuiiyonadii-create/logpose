package com.uriel.logpose.thamis.autonomy

/**
 * FASE 25.19 — THAMIS AUTONOMOUS ASSISTANCE FRAMEWORK
 * FASE 1: AUTONOMY ENGINE CORE
 */
enum class AutonomyLevel {
    LEVEL_0, // Solo respuesta manual
    LEVEL_1, // Sugerencias (Usuario decide)
    LEVEL_2, // Automatizaciones simples aprobadas
    LEVEL_3  // Asistencia avanzada con supervisión
}

object AutonomyEngine {
    private var currentLevel = AutonomyLevel.LEVEL_1

    fun setLevel(level: AutonomyLevel) {
        currentLevel = level
    }

    fun getLevel(): AutonomyLevel = currentLevel

    /**
     * Evalúa si una acción puede ser automatizada bajo el nivel actual.
     */
    fun canAutomate(requiredLevel: AutonomyLevel): Boolean {
        return currentLevel.ordinal >= requiredLevel.ordinal
    }
}
