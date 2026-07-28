package com.uriel.logpose.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.uriel.logpose.R
import com.uriel.logpose.core.app.MainActivity
import com.uriel.logpose.core.services.LogPoseCallService
import com.uriel.logpose.core.receivers.NotificationDismissReceiver

object NotificationHelper {

    const val CHANNEL_ID = "logpose_service"
    const val SUGGESTION_CHANNEL_ID = "trip_suggestion_channel"
    const val SUGGESTION_NOTIFICATION_ID = 2001

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)

            // Canal de Servicio (Informativo)
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "LogPose Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Servicio principal de LogPose"
            }
            manager?.createNotificationChannel(serviceChannel)

            // Canal de sugerencia: con sonido, para que se note al conectar el casco
            val suggestionChannel = NotificationChannel(
                SUGGESTION_CHANNEL_ID,
                "Sugerencia de viaje",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Avisa cuando el casco se conecta, para iniciar el viaje rápido"
                enableVibration(true)
            }
            manager?.createNotificationChannel(suggestionChannel)
        }
    }

    // Mantenemos compatibilidad con código viejo
    fun createNotificationChannel(context: Context) = createChannels(context)

    fun showStartTripSuggestion(context: Context) {
        val startTripIntent = Intent(context, LogPoseCallService::class.java).apply {
            action = LogPoseCallService.ACTION_START_TRIP
        }
        val startPendingIntent = PendingIntent.getService(
            context, 1, startTripIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(context, NotificationDismissReceiver::class.java)
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, 1, dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, SUGGESTION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Casco conectado")
            .setContentText("Tocá para iniciar el viaje con Thamis")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setContentIntent(startPendingIntent) // tocar el cuerpo = iniciar directo
            .addAction(android.R.drawable.ic_media_play, "Iniciar viaje", startPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Ahora no", dismissPendingIntent)
            .setAutoCancel(true)
            .setTimeoutAfter(5 * 60 * 1000L) // se auto-descarta a los 5 min si no se toca
            .build()

        try {
            NotificationManagerCompat.from(context).notify(SUGGESTION_NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Manejar falta de permiso POST_NOTIFICATIONS en Android 13+
        }
    }

    fun cancelSuggestion(context: Context) {
        NotificationManagerCompat.from(context).cancel(SUGGESTION_NOTIFICATION_ID)
    }
}
