package com.uriel.logpose.thamis.decision

import com.thamis.lab.core.contracts.intent.Intent

/**
 * Decision v1.6: Representa una resolución de intención con metadatos de origen y flags de confirmación.
 */
data class Decision(
    val intent: Intent,
    val confidence: Float,
    val entities: Map<String, String> = emptyMap(),
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val intentUri: String? = null,
    val fromAi: Boolean = false,
    val requiresConfirmation: Boolean = false // v1.6: Flag para flujo agéntico/seguridad
)
