package com.uriel.logpose.thamis.personalization.metrics

/**
 * KPIs de personalización y satisfacción.
 */
object PersonalizationMetrics {
    var totalPreferenceChanges = 0
    var implicitLearningsCount = 0
    var userAcceptanceRate = 1.0f

    fun recordChange() { totalPreferenceChanges++ }
    fun recordLearning() { implicitLearningsCount++ }
}
