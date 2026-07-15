package com.uriel.logpose.core.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.app.Notification
import androidx.core.app.NotificationCompat
import com.uriel.logpose.R
import com.uriel.logpose.core.notifications.NotificationHelper

class LogPoseService : Service() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        Log.d("LogPoseService", "Servicio creado")
    }

    private fun createNotification(): Notification {

        return NotificationCompat.Builder(
            this,
            NotificationHelper.CHANNEL_ID
        )
            .setContentTitle("LogPose")
            .setContentText("LogPose está ejecutándose")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        Log.d("LogPoseService", "Servicio iniciado")

        startForeground(
            1,
            createNotification()
        )

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LogPoseService", "Servicio detenido")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}