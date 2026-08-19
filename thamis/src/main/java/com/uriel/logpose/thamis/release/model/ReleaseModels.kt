package com.uriel.logpose.thamis.release.model

import java.util.*

/**
 * Representa una versión pública del sistema THAMIS.
 */
data class BetaVersion(
    val version: String,
    val releaseDate: Long = System.currentTimeMillis(),
    val changes: List<String>,
    val status: ReleaseStatus
)

enum class ReleaseStatus {
    INTERNAL,
    CLOSED_BETA,
    PUBLIC_BETA,
    STABLE
}

/**
 * Sesión de un usuario beta público.
 */
data class BetaUserSession(
    val userId: String,
    val sessionId: String = UUID.randomUUID().toString(),
    val events: List<String>,
    val feedback: String?
)

/**
 * Reporte consolidado de retroalimentación pública.
 */
data class FeedbackReport(
    val issue: String,
    val category: FeedbackCategory,
    val priority: Int,
    var status: String = "OPEN"
)

enum class FeedbackCategory {
    BUG, UX, VOICE, PERFORMANCE, SAFETY, REQUEST
}
