package com.uriel.logpose.thamis.cognitive.engine

import com.uriel.logpose.thamis.cognitive.model.Evidence

/**
 * Modelo para definir reglas de puntuación de THAMIS.
 * Permite que el razonamiento sea dinámico y basado en datos externos.
 */
data class EvidenceRule(
    val name: String,
    val source: Evidence.Source,
    val impact: Float,
    val description: String,
    val ttlMs: Long = 5000L
)
