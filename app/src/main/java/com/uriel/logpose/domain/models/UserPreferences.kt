package com.uriel.logpose.domain.models

/**
 * Domain model for LogPose user settings.
 */
data class UserPreferences(
    val volumeLevel: Int = 10,
    val drivingModeEnabled: Boolean = true,
    val privacyModeEnabled: Boolean = false,
    val selectedIntercomMac: String? = null
)
