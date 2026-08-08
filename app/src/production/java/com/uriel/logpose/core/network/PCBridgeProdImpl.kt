package com.uriel.logpose.core.network

import javax.inject.Inject

/**
 * Production implementation of PCBridge (No-Op).
 */
class PCBridgeProdImpl @Inject constructor() : PCBridge {
    override fun sendCommand(action: String) {
        // No-Op in production for security.
    }

    override fun startRemoteServer() {
        // No-Op
    }

    override fun stopRemoteServer() {
        // No-Op
    }
}
