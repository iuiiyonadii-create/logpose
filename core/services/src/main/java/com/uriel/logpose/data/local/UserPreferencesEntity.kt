package com.uriel.logpose.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey val id: Int = 0,
    val volumeLevel: Int = 10,
    val drivingModeEnabled: Boolean = true,
    val privacyModeEnabled: Boolean = false,
    val selectedIntercomMac: String? = null
)
