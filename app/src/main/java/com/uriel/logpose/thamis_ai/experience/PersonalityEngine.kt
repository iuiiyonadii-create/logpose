package com.uriel.logpose.thamis_ai.experience

import com.uriel.logpose.domain.models.DrivingState

/**
 * Adjusts THAMIS communication style based on context.
 */
class PersonalityEngine {

    fun getPreferredStyle(drivingState: DrivingState): ResponseStyle {
        return if (drivingState == DrivingState.RIDING) {
            ResponseStyle.MINIMAL
        } else {
            ResponseStyle.NORMAL
        }
    }
}
