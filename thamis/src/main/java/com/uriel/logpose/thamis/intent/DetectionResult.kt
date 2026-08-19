package com.uriel.logpose.thamis.intent

import com.thamis.lab.core.contracts.intent.Intent

/**
 * Resultado de la detección de intención.
 * v6.9: Agregado soporte para Tipos de Target (INDEXED/FREE_TEXT) y Privacy Gates.
 */
data class DetectionResult(
    val intent: Intent,
    val score: Float,
    val entities: Map<String, String> = emptyMap(),
    val type: String = "INDEXED_MATCH",
    val intentUri: String? = null
)
