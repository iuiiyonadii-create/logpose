package com.uriel.logpose.core.services

import android.content.ComponentName
import android.content.Context
import android.service.notification.NotificationListenerService
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * ListenerWatchdog: Vigila la salud del NotificationListenerService.
 * Si detecta que el servicio murió, intenta reconectarlo y avisa al usuario si falla.
 */
class ListenerWatchdog(private val context: Context) {
    private var consecutiveFailures = 0
    private var lastAlertTimestamp = 0L
    private var hasAlertedActiveIssue = false

    companion object {
        const val HEARTBEAT_TIMEOUT_MS = 90_000L
        const val FAILURE_THRESHOLD = 3 
        const val REALERT_COOLDOWN_MS = 5 * 60_000L 
    }

    fun checkAndHandle(lastHeartbeat: Long) {
        val now = System.currentTimeMillis()
        
        // SINCRO CLAUDE: Si la instancia existe, está conectada (el heartbeat se actualiza en CallService)
        val isInstanceAlive = LogPoseNotificationListener.getInstance() != null
        val isStale = lastHeartbeat > 0 && (now - lastHeartbeat) > HEARTBEAT_TIMEOUT_MS

        if (isInstanceAlive && !isStale) {
            // El listener está sano
            if (hasAlertedActiveIssue) {
                AlertManager.enqueue("Thamis reconectada.", AlertPriority.NORMAL)
                hasAlertedActiveIssue = false
            }
            consecutiveFailures = 0
            return
        }

        // Si no hay instancia o el pulso es muy viejo, intentamos rebind silencioso
        if (consecutiveFailures % 5 == 0) { // Solo logueamos cada 5 intentos para no ensuciar el Logcat
            LogPoseLogger.d("Watchdog: Listener inactivo. Intentando recuperación silenciosa.")
        }
        requestRebind()
        consecutiveFailures++

        // Si falló varias veces, avisar al usuario
        if (consecutiveFailures >= FAILURE_THRESHOLD &&
            now - lastAlertTimestamp > REALERT_COOLDOWN_MS
        ) {
            val isEnabled = com.uriel.logpose.features.music.NotificationAccessHelper.isEnabled(context)
            if (!isEnabled) {
                LogPoseLogger.e("Watchdog: Permiso desactivado.")
                AlertManager.enqueue(AlertMessage("Permiso de notificaciones desactivado.", priority = AlertPriority.HIGH))
                lastAlertTimestamp = now
                hasAlertedActiveIssue = true
            } else {
                // Si el permiso está pero no hay instancia, solo intentamos rebind silencioso
                LogPoseLogger.w("Watchdog: Intentando recuperar Thamis silenciosamente...")
                requestRebind()
            }
        }
    }

    private fun requestRebind() {
        try {
            val componentName = ComponentName(context, LogPoseNotificationListener::class.java)
            NotificationListenerService.requestRebind(componentName)
        } catch (e: Exception) {
            LogPoseLogger.e("Watchdog: Error en requestRebind: ${e.message}")
        }
    }
}
