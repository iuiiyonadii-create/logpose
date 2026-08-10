package com.uriel.logpose.thamis_ai.analytics

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * High-level coordinator for the metrics system.
 */
class AnalyticsManager {

    private val privacyFilter = PrivacyFilter()

    fun trackEvent(event: String, data: Map<String, Any>) {
        val safeData = privacyFilter.filter(data)
        LogPoseLogger.d("Analytics", "Tracking: $event with data: $safeData")
    }
}
