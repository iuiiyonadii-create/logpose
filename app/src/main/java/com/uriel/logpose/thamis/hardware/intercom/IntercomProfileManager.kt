package com.uriel.logpose.thamis.hardware.intercom

import com.uriel.logpose.thamis.hardware.model.IntercomProfile

/**
 * Gestiona los perfiles de intercomunicadores y cascos inteligentes.
 */
object IntercomProfileManager {
    private val profiles = mutableMapOf<String, IntercomProfile>()

    fun registerProfile(profile: IntercomProfile) {
        profiles[profile.modelName] = profile
    }

    fun getProfileForDevice(name: String): IntercomProfile {
        return profiles[name] ?: IntercomProfile(
            modelName = "Generic",
            averageLatencyMs = 250,
            audioQualityScore = 0.7f,
            compatibilityMode = "LEGACY_SCO"
        )
    }

    fun getAllProfiles(): List<IntercomProfile> = profiles.values.toList()
}
