package com.uriel.logpose.core.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

/**
 * LogPose Application
 * Initializes Hilt and system-wide components.
 */
@HiltAndroidApp
class LogPoseApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        instance = this
        AppContainer.init(this)
        container = AppContainer.instance
        
        // Inicializar Singletons que requieren Contexto
        AppManager.initialize(this)
        com.uriel.logpose.core.services.AlertManager.initialize(this)
        com.uriel.logpose.features.voice.FeedbackManager.initialize(this)
        com.uriel.logpose.features.music.MusicManager.initialize(this)
        com.uriel.logpose.features.voice.VoiceManager.initialize(this, AppContainer.instance.voiceRepository)

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                SERVICE_CHANNEL_ID,
                "LogPose Active Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Used for background riding assistance"
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        const val SERVICE_CHANNEL_ID = "logpose_service_channel"
        lateinit var instance: LogPoseApplication
            private set
    }
}
