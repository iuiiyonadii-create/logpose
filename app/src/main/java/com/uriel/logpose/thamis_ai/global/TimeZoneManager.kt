package com.uriel.logpose.thamis_ai.global

import java.util.TimeZone

/**
 * Handles time-sensitive logic across different regions.
 */
class TimeZoneManager {
    fun getCurrentTimeZone(): TimeZone = TimeZone.getDefault()
}
