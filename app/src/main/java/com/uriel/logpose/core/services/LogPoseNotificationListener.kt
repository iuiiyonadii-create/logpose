package com.uriel.logpose.core.services

import android.content.ComponentName
import android.content.Context
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.uriel.logpose.core.compat.core.LogPoseLogger
import java.util.concurrent.atomic.AtomicLong

/**
 * LogPoseNotificationListener v9.7: El Guardián Inteligente.
 * Restaurado con lógica de Juez Fonético y protección HyperOS.
 */
class LogPoseNotificationListener : NotificationListenerService() {

    private val lastPipelineStartTime = AtomicLong(0)
    private val GRACE_WINDOW_MS = 5000L

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.i("LogPose_NL", "¡LogPoseNotificationListener CREADO! 👁️")
    }

    companion object {
        const val SPOTIFY_PACKAGE = "com.spotify.music"
        @Volatile private var instance: LogPoseNotificationListener? = null
        @Volatile private var lastHeartbeat = 0L
        private var rebindRetryCount = 0
        
        fun getInstance(): LogPoseNotificationListener? = instance

        fun updateHeartbeat() { lastHeartbeat = System.currentTimeMillis() }
        fun getLastHeartbeat(): Long = lastHeartbeat

        fun isPermissionGranted(context: Context): Boolean {
            val enabledListeners =
                android.provider.Settings.Secure.getString(
                    context.contentResolver,
                    "enabled_notification_listeners"
                ) ?: return false

            val component =
                ComponentName(
                    context,
                    LogPoseNotificationListener::class.java
                )

            val expected = component.flattenToString()

            Log.d("LogPose_NL", "Esperado: $expected")
            Log.d("LogPose_NL", "Sistema: $enabledListeners")

            return enabledListeners
                .split(":")
                .any {
                    it.equals(expected, ignoreCase = true)
                }
        }

        fun tryForceRebind(context: Context) {
            val hasPermission = isPermissionGranted(context)
            Log.d("LogPose_NL", "Intento de rebind. Permiso: $hasPermission, Instancia: ${instance != null}")
            
            if (hasPermission && instance == null && rebindRetryCount < 3) {
                rebindRetryCount++
                Log.w("LogPose_NL", "¡DESFIBRILADOR! Intento $rebindRetryCount/3 de recuperar al Juez.")
                try {
                    requestRebind(ComponentName(context, LogPoseNotificationListener::class.java))
                } catch (e: Exception) { 
                    Log.e("LogPose_NL", "Fallo rebind crítico: ${e.message}") 
                }
            }
        }

        fun getSpotifyController(context: Context): android.media.session.MediaController? {
            val svc = instance
            if (svc == null) {
                Log.e("LogPose_NL", "getSpotifyController: INSTANCIA NULA.")
                tryForceRebind(context)
                return null
            }
            return try {
                val sm = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
                val component = ComponentName(context, LogPoseNotificationListener::class.java)
                val sessions = sm.getActiveSessions(component)
                sessions.find { it.packageName == SPOTIFY_PACKAGE }
            } catch (e: Exception) { null }
        }

        fun dismissMapsNavigation() {
            val svc = instance ?: return
            try {
                for (sbn in svc.activeNotifications) {
                    if (sbn.packageName == "com.google.android.apps.maps") {
                        svc.tryClickStop(sbn)
                    }
                }
            } catch (e: Exception) {
                Log.e("LogPose_NL", "Error quitando navegación: ${e.message}")
            }
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        updateHeartbeat()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        rebindRetryCount = 0
        Log.i("LogPose_NL", "¡LogPose Service CONECTADO Y LISTO! ✅")
        
        // v15.0: Escuchar todas las sesiones de medios (YouTube Music, Spotify, etc.)
        setupMediaSessionListener()
    }

    private fun setupMediaSessionListener() {
        val sm = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val component = ComponentName(this, LogPoseNotificationListener::class.java)
        
        sm.addOnActiveSessionsChangedListener({ controllers ->
            controllers?.forEach { controller ->
                controller.registerCallback(object : android.media.session.MediaController.Callback() {
                    override fun onMetadataChanged(metadata: android.media.MediaMetadata?) {
                        metadata?.let { meta ->
                            val artist = meta.getString(android.media.MediaMetadata.METADATA_KEY_ARTIST) ?: ""
                            val title = meta.getString(android.media.MediaMetadata.METADATA_KEY_TITLE) ?: ""
                            val album = meta.getString(android.media.MediaMetadata.METADATA_KEY_ALBUM) ?: ""

                            if (artist.isNotBlank() || title.isNotBlank()) {
                                LogPoseLogger.i("🧬 ADN Musical (Omnicanal): Aprendiendo de ${controller.packageName} -> $artist - $title")
                                com.uriel.logpose.thamis.learning.LearningEngine.learnMusicEntity(artist)
                                com.uriel.logpose.thamis.learning.LearningEngine.learnMusicEntity(title)
                                if (artist.isNotBlank() && title.isNotBlank()) {
                                    com.uriel.logpose.thamis.learning.LearningEngine.learnTrackArtistRelation(title, artist)
                                    com.uriel.logpose.thamis.learning.LearningEngine.addFavoriteArtist(artist)
                                }
                            }
                        }
                    }
                })
            }
        }, component)
    }

    override fun onListenerDisconnected() { 
        super.onListenerDisconnected()
        instance = null 
    }
    
    override fun onDestroy() { 
        super.onDestroy()
        instance = null 
    }

    private fun tryClickStop(sbn: StatusBarNotification) {
        val actions = sbn.notification.actions ?: return
        for (action in actions) {
            val title = action.title?.toString()?.lowercase() ?: ""
            if (title.contains("stop") || title.contains("salir") || title.contains("finalizar")) {
                try { 
                    action.actionIntent.send() 
                } catch (e: Exception) {
                    LogPoseLogger.e("NotificationListener: Error al ejecutar acción remota: ${e.message}")
                }
            }
        }
    }

    fun notifyPipelineStarted(query: String) {
        lastPipelineStartTime.set(System.currentTimeMillis())
        Log.d("LogPose_NL", "Ventana de gracia activa para: $query")
    }
}
