package com.uriel.logpose.thamis.navigation.validation

import java.util.UUID

/**
 * Registra los datos de una sesión de validación de navegación.
 */
data class NavigationValidationSession(
    val id: String = UUID.randomUUID().toString(),
    val startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
    var averageSpeedKmh: Int = 0,
    var maxSpeedKmh: Int = 0,
    var totalCommands: Int = 0,
    var executedCommands: Int = 0,
    var confirmedCommands: Int = 0,
    var rejectedCommands: Int = 0,
    var expiredCommands: Int = 0,
    var gpsAvailable: Boolean = true,
    var signalQuality: Float = 1.0f // 0.0 a 1.0
) {
    val durationMs: Long
        get() = (endTime ?: System.currentTimeMillis()) - startTime
}
