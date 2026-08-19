package com.uriel.logpose.thamis.cognitive.model

/**
 * El estado interno del cerebro THAMIS.
 * Diferente de WorldState porque aquí viven las intenciones y la memoria.
 */
data class MentalState(
    val activeGoal: Goal?,
    val attentionLevel: Float, // ¿Estamos esperando una respuesta?
    val pendingDoubts: List<String>,
    val workingMemory: List<Experience>
)

/**
 * Representa una unidad de memoria de corto plazo (Episódica).
 */
data class Experience(
    val goal: Goal,
    val result: Result,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class Result { SUCCESS, USER_CORRECTED, SYSTEM_ERROR }
}
