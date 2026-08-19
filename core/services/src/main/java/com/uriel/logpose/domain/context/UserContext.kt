package com.uriel.logpose.domain.context

import com.uriel.logpose.domain.models.BluetoothState

/**
 * Domain model representing the user's active configuration and connectivity.
 */
data class UserContext(
    val bluetoothState: BluetoothState,
    val isMusicPlaying: Boolean = false,
    val isPrivacyModeActive: Boolean = false,
    val activeApp: String? = null
)
