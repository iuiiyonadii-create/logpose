package com.uriel.logpose.core.services

import android.content.Context

/**
 * ListenerWatchdog: Vigila la salud del NotificationListenerService.
 * v2.0: Desactivado para evitar colisiones con el nuevo pipeline Always-On.
 */
class ListenerWatchdog(private val context: Context) {
    fun checkAndHandle(lastHeartbeat: Long) {
        // Misión #036: Bypass total para estabilizar el búnker.
    }
}
