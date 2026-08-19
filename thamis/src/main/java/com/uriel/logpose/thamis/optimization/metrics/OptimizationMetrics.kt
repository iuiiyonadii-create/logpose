package com.uriel.logpose.thamis.optimization.metrics

/**
 * KPIs de optimización de recursos.
 */
object OptimizationMetrics {
    var initialMemoryKb = 0L
    var finalMemoryKb = 0L
    var totalImprovementPct = 0f
    var processingTimeSumMs = 0L
    var cyclesCount = 0

    fun recordCycle(improvement: Float, timeMs: Long) {
        cyclesCount++
        totalImprovementPct = (totalImprovementPct * (cyclesCount - 1) + improvement) / cyclesCount
        processingTimeSumMs += timeMs
    }

    fun getAverageImprovement(): Float = totalImprovementPct
}
