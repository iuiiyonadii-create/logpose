package com.uriel.logpose.thamis_ai.connectivity

import android.util.Log

/**
 * Orchestrates external service connections.
 */
class ConnectivityManager {

    fun connect(serviceId: String) {
        Log.d("Connectivity", "Connecting to: $serviceId")
    }

    fun disconnect(serviceId: String) {
        Log.d("Connectivity", "Disconnecting: $serviceId")
    }
}
