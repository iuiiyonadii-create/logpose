package com.uriel.logpose.services

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import com.uriel.logpose.core.app.LogPoseApplication

/**
 * Manages notifications for LogPose foreground services.
 */
class ServiceNotificationManager(private val context: Context) {

    fun createForegroundNotification(statusText: String): Notification {
        return NotificationCompat.Builder(context, LogPoseApplication.SERVICE_CHANNEL_ID)
            .setContentTitle("LogPose Safe Riding")
            .setContentText(statusText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
