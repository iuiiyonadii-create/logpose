package com.uriel.logpose.core.network

import com.uriel.logpose.core.app.LogPoseApplication

/**
 * Centralized network configuration and security.
 */
object NetworkConfig {
    /**
     * API Key for authenticating with the PC Proxy.
     */
    const val THAMIS_API_KEY = "LOGPOSE_THAMIS_v1_SECURE"

    /**
     * Returns the current PC IP from settings or default.
     */
    fun getPCIp(): String {
        return LogPoseApplication.entryPoint.settingsManager().getString("pc_ip", "192.168.1.33") ?: "192.168.1.33"
    }

    const val PC_PORT = 9999
    const val PC_CONTROL_PORT = 5051
}
