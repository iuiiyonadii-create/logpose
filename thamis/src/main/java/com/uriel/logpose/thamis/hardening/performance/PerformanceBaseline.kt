package com.uriel.logpose.thamis.hardening.performance

import com.uriel.logpose.thamis.hardening.model.PerformanceTarget

/**
 * Define los umbrales de rendimiento aceptables para el producto final.
 */
object PerformanceBaseline {

    private val targets = mapOf(
        "CORE_STARTUP" to PerformanceTarget("App Startup", 1500, 3000, 5120),
        "VOICE_RESPONSE" to PerformanceTarget("Voice Response", 300, 1000, 2048),
        "PLANNING_GEN" to PerformanceTarget("Plan Generation", 50, 200, 1024),
        "WORLD_SYNC" to PerformanceTarget("World Sync", 10, 50, 512)
    )

    fun getTarget(operation: String): PerformanceTarget? = targets[operation]

    fun checkCompliance(operation: String, actualMs: Long): Boolean {
        val target = targets[operation] ?: return true
        return actualMs <= target.targetMs
    }
}
