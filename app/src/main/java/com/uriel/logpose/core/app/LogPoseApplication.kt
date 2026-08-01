package com.uriel.logpose.core.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.uriel.logpose.core.compat.core.LogPoseLogger
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
        try {
            AppManager.initialize(this)
            com.uriel.logpose.features.app.AppManager.initialize(this)
            com.uriel.logpose.features.voice.CallManager.initialize(this)
            com.uriel.logpose.core.services.AlertManager.initialize(this)
            com.uriel.logpose.features.voice.FeedbackManager.initialize(this)
            com.uriel.logpose.features.music.MusicManager.initialize(this)
            com.uriel.logpose.features.voice.VoiceManager.initialize(this, AppContainer.instance.voiceRepository)
            
            // SINCRO: Cargar base de datos lingüística (NLU)
            com.uriel.logpose.core.nlp.LanguageRepository.initialize(this)
            
            // SINCRO: Iniciar servidor de control remoto para el laboratorio
            com.uriel.logpose.core.network.PCControlServer.start()
            
            LogPoseLogger.i("LogPoseApplication: Todos los sistemas inicializados.")
        } catch (e: Exception) {
            LogPoseLogger.e("LogPoseApplication: Error crítico en inicialización: ${e.message}")
        }

        createNotificationChannels()
    }

    fun createNotificationChannels() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                
                // Canal Principal de Servicio
                val serviceChannel = NotificationChannel(
                    SERVICE_CHANNEL_ID,
                    "LogPose Active Service",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Used for background riding assistance"
                    setShowBadge(false)
                }
                manager.createNotificationChannel(serviceChannel)

                // Canal de Viaje (Crítico para el S8)
                val tripChannelId = com.uriel.logpose.core.services.LogPoseCallService.TRIP_CHANNEL_ID
                val tripChannel = NotificationChannel(
                    tripChannelId,
                    "LogPose Trip Status",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Shows active trip information"
                    enableLights(false)
                }
                manager.createNotificationChannel(tripChannel)
                
                LogPoseLogger.i("THAMIS_LAB: Canales de notificación blindados creados exitosamente.")
            }
        } catch (e: Exception) {
            LogPoseLogger.e("THAMIS_LAB: Fallo en creación de canales. Dispositivo puede estar saturado: ${e.message}")
        }
    }

    companion object {
        const val SERVICE_CHANNEL_ID = "logpose_service_channel"
        lateinit var instance: LogPoseApplication
            private set
    }
}
