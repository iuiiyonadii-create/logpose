package com.uriel.logpose.thamis.proactive.model

import java.util.*

/**
 * Decisión del asistente sobre iniciar una interacción proactiva.
 */
data class ProactiveDecision(
    val action: ProactiveAction,
    val reason: String,
    val confidence: Float,
    val priority: Int,
    val contextSnapshotId: String?
)

enum class ProactiveAction { SUGGEST, WAIT, IGNORE, CANCEL }

/**
 * Contenido de la sugerencia propuesta.
 */
data class Suggestion(
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    val benefit: String,
    val urgency: Int, // 0 to 100
    val expirationMs: Long = 30000L
)

/**
 * Política que gobierna el comportamiento proactivo.
 */
data class ProactivePolicy(
    val condition: String,
    val isPermitted: Boolean,
    val restriction: String?
)

/**
 * Prioridades para el motor proactivo.
 */
enum class ProactivePriority {
    CRITICAL, IMPORTANT, USEFUL, OPTIONAL, SILENT
}
