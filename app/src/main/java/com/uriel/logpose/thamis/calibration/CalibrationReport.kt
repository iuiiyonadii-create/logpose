package com.uriel.logpose.thamis.calibration

import com.uriel.logpose.thamis.shadow.ShadowResult

/**
 * El veredicto del CalibrationEngine sobre una versión específica de THAMIS.
 */
data class CalibrationReport(
    val date: Long = System.currentTimeMillis(),
    val engineVersion: String,
    val totalSamples: Int,
    val matchCount: Int,
    val divergenceCount: Int,
    val accuracyPercentage: Float,
    val criticalErrors: Int,
    val memoryBoostContribution: Float = 0f,
    val decayImpactRate: Float = 0f,
    val intentClassificationAccuracy: Float = 0f,
    val recommendations: List<CalibrationRecommendation>,
    val bestDecisions: List<ShadowResult>,
    val worstDecisions: List<ShadowResult>
)
