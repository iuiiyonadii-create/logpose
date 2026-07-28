package com.uriel.logpose.thamis_ai.global

/**
 * Adapts measurements, time formats, and cultural nuances.
 */
class LocalizationManager {
    fun formatDistance(meters: Float): String {
        return if (isMetricSystem()) "${meters / 1000} km" else "${meters * 0.000621371f} miles"
    }

    private fun isMetricSystem() = true
}
