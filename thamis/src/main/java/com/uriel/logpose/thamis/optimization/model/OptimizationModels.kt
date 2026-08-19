package com.uriel.logpose.thamis.optimization.model

import java.util.*

/**
 * Estado actual de los recursos del sistema THAMIS.
 */
data class ResourceState(
    val memoryUsageKb: Long,
    val activeObjects: Int,
    val cpuLoadFactor: Float, // 0.0 to 1.0
    val systemStatus: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Plan de optimización propuesto por el motor.
 */
data class OptimizationPlan(
    val id: String = UUID.randomUUID().toString(),
    val affectedResource: ResourceType,
    val strategy: String,
    val risk: Float, // 0.0 to 1.0
    val priority: Int,
    val expectedOutcome: String
)

enum class ResourceType { MEMORY, CPU, ENERGY, CACHE, STORAGE }

/**
 * Captura puntual del estado de la memoria.
 */
data class MemorySnapshot(
    val usedKb: Long,
    val growthRate: Float,
    val trend: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Captura puntual del estado energético.
 */
data class EnergySnapshot(
    val activityLevel: Float, // 0.0 to 1.0
    val processFrequencyHz: Int,
    val estimatedConsumptionMa: Float,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Resultado de una acción de optimización.
 */
data class OptimizationResult(
    val planId: String,
    val improvementPct: Float,
    val processingTimeMs: Long,
    val systemImpact: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Prioridades para el planificador de recursos.
 */
enum class ResourcePriority {
    CRITICAL, HIGH, NORMAL, LOW, BACKGROUND
}
