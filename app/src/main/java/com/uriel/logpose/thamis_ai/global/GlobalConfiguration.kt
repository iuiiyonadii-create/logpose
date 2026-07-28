package com.uriel.logpose.thamis_ai.global

/**
 * Global feature flags and system-wide regional settings.
 */
data class GlobalConfiguration(
    val serverEndpoint: String = "https://api.logpose.com",
    val updateChannel: String = "stable"
)
