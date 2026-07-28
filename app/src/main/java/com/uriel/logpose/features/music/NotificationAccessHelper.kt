package com.uriel.logpose.features.music

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService
import androidx.core.app.NotificationManagerCompat

/**
 * Detecta y guía al usuario para habilitar el permiso de Notification Access.
 */
object NotificationAccessHelper {

    fun isEnabled(context: Context): Boolean {
        val enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(context)
        return context.packageName in enabledPackages
    }

    fun openSettings(context: Context) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun requestRebind(context: Context) {
        val component = ComponentName(context, com.uriel.logpose.core.services.LogPoseNotificationListener::class.java)
        // Fix: Usamos la clase base del sistema para el rebind
        NotificationListenerService.requestRebind(component)
    }
}
