package com.uriel.logpose.thamis.optimization.memory

import com.uriel.logpose.thamis.optimization.model.MemorySnapshot
import com.uriel.logpose.thamis.optimization.model.OptimizationPlan
import com.uriel.logpose.thamis.optimization.model.ResourceType

/**
 * Analiza el crecimiento de memoria y detecta posibles fugas en el núcleo de THAMIS.
 */
object MemoryOptimizer {
    private val history = mutableListOf<MemorySnapshot>()

    fun recordSnapshot(usedKb: Long) {
        val growth = if (history.isNotEmpty()) (usedKb - history.last().usedKb).toFloat() else 0f
        val trend = when {
            growth > 1024 -> "STEEP_GROWTH"
            growth > 0 -> "GROWING"
            growth < 0 -> "REDUCING"
            else -> "STABLE"
        }
        
        val snapshot = MemorySnapshot(usedKb, growth, trend)
        history.add(snapshot)
        if (history.size > 50) history.removeAt(0)
    }

    fun analyze(): OptimizationPlan? {
        if (history.size < 10) return null
        
        val lastTen = history.takeLast(10)
        val continuousGrowth = lastTen.all { it.growthRate >= 0 }
        
        return if (continuousGrowth && lastTen.last().usedKb > 5120) { // > 5MB
            OptimizationPlan(
                affectedResource = ResourceType.MEMORY,
                strategy = "CLEAR_OLD_SNAPSHOTS",
                risk = 0.2f,
                priority = 50,
                expectedOutcome = "Reduced footprint by 20%"
            )
        } else null
    }

    fun getTrend(): String = history.lastOrNull()?.trend ?: "UNKNOWN"
}
