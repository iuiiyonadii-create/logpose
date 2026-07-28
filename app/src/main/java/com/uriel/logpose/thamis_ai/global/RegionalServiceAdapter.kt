package com.uriel.logpose.thamis_ai.global

/**
 * Maps THAMIS requests to region-specific service providers.
 */
class RegionalServiceAdapter {
    fun getMapsProvider(region: String): String {
        return if (region == "CN") "Baidu" else "Google"
    }
}
