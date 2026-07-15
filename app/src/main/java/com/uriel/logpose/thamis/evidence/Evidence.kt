package com.uriel.logpose.thamis.evidence

import java.util.UUID

/**
 * Representa un hecho conocido por THAMIS.
 */
data class Evidence(

    val id: UUID = UUID.randomUUID(),

    val type: EvidenceType,

    val source: EvidenceSource,

    val value: String,

    val confidence: Float,

    val timestamp: Long = System.currentTimeMillis()

)