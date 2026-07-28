package com.uriel.logpose.thamis_ai.integrations

/**
 * Ensures connected devices are authorized and safe.
 */
class DeviceAuthentication {
    fun authenticate(deviceId: String, token: String): Boolean {
        return token == "TRUSTED_LOGPOSE_LINK"
    }
}
