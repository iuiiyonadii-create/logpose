package com.uriel.logpose.thamis.feedback.metrics

import com.uriel.logpose.thamis.feedback.model.FeedbackCategory

/**
 * KPIs del sistema de feedback e inteligencia de producto.
 */
object FeedbackMetrics {
    var totalReports = 0
    var bugReports = 0
    var safetyReports = 0
    var insightsGenerated = 0
    var improvementsProposed = 0

    fun record(category: FeedbackCategory) {
        totalReports++
        when (category) {
            FeedbackCategory.BUG -> bugReports++
            FeedbackCategory.SAFETY -> safetyReports++
            else -> {}
        }
    }

    fun recordInsight() { insightsGenerated++ }
    fun recordProposal() { improvementsProposed++ }
}
