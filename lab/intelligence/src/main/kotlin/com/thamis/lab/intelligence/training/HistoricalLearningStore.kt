package com.thamis.lab.intelligence.training

import java.util.concurrent.CopyOnWriteArrayList

public data class HistoricalExecutionRecord(
    public val recordId: String,
    public val timestampMs: Long,
    public val qualityScore: Double,
    public val executionDurationMs: Long,
    public val isSuccess: Boolean
)

public data class LearningInsight(
    public val totalCampaignsAnalyzed: Int,
    public val averageQualityScore: Double,
    public val regressionDetected: Boolean,
    public val topRecommendation: String
)

/**
 * Historical Learning Store accumulating real campaign telemetry, logcats, crashes, and evaluating historical regression trends.
 */
public class HistoricalLearningStore {
    private val history = CopyOnWriteArrayList<HistoricalExecutionRecord>()

    public fun recordCampaignExecution(record: HistoricalExecutionRecord) {
        history.add(record)
    }

    public fun generateInsights(): LearningInsight {
        if (history.isEmpty()) {
            return LearningInsight(
                totalCampaignsAnalyzed = 0,
                averageQualityScore = 0.0,
                regressionDetected = false,
                topRecommendation = "Run campaigns to gather baseline evidence."
            )
        }

        val avgScore = history.map { it.qualityScore }.average()
        val latest = history.last()
        val regression = latest.qualityScore < (avgScore - 5.0)

        val rec = if (regression) {
            "Regression detected in latest run. Review recent process crashes or memory allocation."
        } else {
            "System quality stable at ${String.format("%.1f", avgScore)}/100."
        }

        return LearningInsight(
            totalCampaignsAnalyzed = history.size,
            averageQualityScore = avgScore,
            regressionDetected = regression,
            topRecommendation = rec
        )
    }

    public fun clear() {
        history.clear()
    }
}
