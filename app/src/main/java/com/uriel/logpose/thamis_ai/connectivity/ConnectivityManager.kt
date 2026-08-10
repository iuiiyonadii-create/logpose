package com.uriel.logpose.thamis_ai.connectivity

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Orchestrates external service connections.
 */
class ConnectivityManager {

    fun connect(serviceId: String) {
        LogPoseLogger.d("Connectivity", "Connecting to: $serviceId")
    }

    fun disconnect(serviceId: String) {
        LogPoseLogger.d("Connectivity", "Disconnecting: $serviceId")
    }
}
