package com.uriel.logpose.thamis.hardware.model

import java.util.*

/**
 * Representa el perfil técnico de un componente de hardware.
 */
data class HardwareProfile(
    val id: String = UUID.randomUUID().toString(),
    val deviceName: String,
    val connectionType: String,
    val capabilities: List<String>,
    val settings: Map<String, Any> = emptyMap()
)

/**
 * Perfil específico para intercomunicadores de casco.
 */
data class IntercomProfile(
    val modelName: String,
    val averageLatencyMs: Long,
    val audioQualityScore: Float, // 0.0 to 1.0
    val compatibilityMode: String
)

/**
 * Foto instantánea del estado de la conexión inalámbrica.
 */
data class ConnectionSnapshot(
    val state: String,
    val connectionTimeMs: Long,
    val errorCount: Int,
    val stabilityIndex: Float, // 0.0 to 1.0
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Informe de rendimiento de audio en el entorno real.
 */
data class AudioPerformanceReport(
    val latencyMs: Long,
    val qualityScore: Float,
    val packetLossRate: Float,
    val detectedIssues: List<String>
)
