package com.uriel.logpose.thamis.security.monitor

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Supervición de conexiones externas sin inspección de contenido.
 */
object NetworkActivityMonitor {

    fun recordConnection(destination: String, module: String) {
        LogPoseLogger.i("THAMIS_NETWORK: Conexión externa detectada por $module hacia $destination")
    }

    fun isConnectionSafe(destination: String): Boolean {
        // En v1.0, solo permitimos dominios conocidos de Google y Spotify
        return destination.contains("google.com") || 
               destination.contains("spotify.com") || 
               destination.contains("api.whatsapp.com")
    }
}
