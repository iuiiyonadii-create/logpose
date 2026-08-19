package com.uriel.logpose.thamis.capabilities

/**
 * Describe una capacidad cognitiva del cerebro THAMIS.
 */
data class CapabilityDescriptor(
    val name: String,
    val type: CapabilityType,
    val version: String,
    val status: CapabilityStatus,
    val authorityEnabled: Boolean,
    val provider: String,
    val validator: String,
    val actuator: String,
    val availability: Boolean,
    val lastUsedTimestamp: Long = 0,
    val metrics: Map<String, String> = emptyMap(),
    val averageLatencyMs: Long = 0,
    val errorCount: Int = 0,
    val healthScore: Float = 1.0f
)
