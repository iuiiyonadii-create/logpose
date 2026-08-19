package com.uriel.logpose.thamis.optimization.monitor

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.optimization.cleanup.CleanupManager
import com.uriel.logpose.thamis.optimization.memory.MemoryOptimizer
import com.uriel.logpose.thamis.optimization.model.OptimizationPlan
import com.uriel.logpose.thamis.optimization.model.ResourceState

/**
 * Motor central de optimización de recursos de THAMIS v1.0.
 */
object ResourceOptimizationEngine {

    fun runOptimizationCycle(currentState: ResourceState): List<OptimizationPlan> {
        LogPoseLogger.d("THAMIS_OPTIMIZATION: Iniciando ciclo de optimización...")
        val plans = mutableListOf<OptimizationPlan>()

        // 1. Analizar Memoria
        MemoryOptimizer.recordSnapshot(currentState.memoryUsageKb)
        MemoryOptimizer.analyze()?.let { plans.add(it) }

        // 2. Ejecutar limpiezas inmediatas si el riesgo es bajo
        plans.forEach { plan ->
            if (plan.risk < 0.3f) {
                CleanupManager.execute(plan)
            }
        }

        return plans
    }
}
