package com.uriel.logpose.features.settings.model

/**
 * Domain model for User Settings.
 */
data class UserSettings(
    val volumeLevel: Int = 10,
    val drivingMode: DrivingMode = DrivingMode.NORMAL,
    val privacyMode: Boolean = false,
    val voiceEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true
)

enum class DrivingMode {
    NORMAL,
    FOCUS,
    PRIVATE
}
