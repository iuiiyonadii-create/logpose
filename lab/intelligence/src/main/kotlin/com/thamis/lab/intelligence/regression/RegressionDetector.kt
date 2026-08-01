package com.thamis.lab.intelligence.regression

public data class RegressionReport(
    public val isRegressionDetected: Boolean,
    public val accuracyDeltaPercent: Double,
    public val latencyDeltaMs: Long,
    public val warningMessage: String?
)

/**
 * Regression Detector for comparing version metrics against historical baselines.
 */
public class RegressionDetector(
    public val maxAllowedAccuracyDropPercent: Double = 2.0,
    public val maxAllowedLatencyIncreaseMs: Long = 20L
) {
    public fun compareVersions(
        baselineAccuracy: Double,
        currentAccuracy: Double,
        baselineLatencyMs: Long,
        currentLatencyMs: Long
    ): RegressionReport {
        val accuracyDelta = baselineAccuracy - currentAccuracy
        val latencyDelta = currentLatencyMs - baselineLatencyMs

        val isRegression = accuracyDelta > maxAllowedAccuracyDropPercent || latencyDelta > maxAllowedLatencyIncreaseMs

        val warning = if (isRegression) {
            "Regression detected: Accuracy drop ${accuracyDelta}%, Latency increase ${latencyDelta}ms"
        } else null

        return RegressionReport(
            isRegressionDetected = isRegression,
            accuracyDeltaPercent = accuracyDelta,
            latencyDeltaMs = latencyDelta,
            warningMessage = warning
        )
    }
}
