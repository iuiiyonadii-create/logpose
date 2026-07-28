package com.uriel.logpose.features.music

import android.content.ComponentName
import android.content.Context
import android.media.browse.MediaBrowser
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log

/**
 * Tier 1 de warm-up: Despierta el proceso de Spotify usando MediaBrowser estándar.
 */
object SpotifyWarmUpBinder {

    private const val TAG = "SpotifyWarmUpBinder"

    @Volatile
    private var browser: MediaBrowser? = null

    fun bind(context: Context, onRejected: () -> Unit = {}) {
        if (browser?.isConnected == true) return

        // ESTRATEGIA CLAUDE: Pequeño delay de asentamiento (300ms) 
        // para evitar la colisión de AppOps tras un reinicio de proceso.
        Handler(Looper.getMainLooper()).postDelayed({
            executeBind(context, onRejected)
        }, 300)
    }

    private fun executeBind(context: Context, onRejected: () -> Unit) {
        // COORDINACIÓN: Duckeamos preventivamente
        com.uriel.logpose.core.services.ComfortNoiseManager.duck()

        // TRUCO EXTRA: Intent fantasma para despertar el proceso antes del bind
        val wakeupIntent = android.content.Intent("com.spotify.music.service.NOTIFY_BROADCAST")
        wakeupIntent.setPackage(com.uriel.logpose.core.services.LogPoseNotificationListener.SPOTIFY_PACKAGE)
        context.sendBroadcast(wakeupIntent)

        val callback = object : MediaBrowser.ConnectionCallback() {
            override fun onConnected() {
                Log.d(TAG, "Tier 1: Bind exitoso.")
                disconnect()
            }

            override fun onConnectionFailed() {
                Log.w(TAG, "Tier 1: Rechazado o fallido.")
                disconnect()
                onRejected()
            }
        }

        try {
            val mediaBrowser = MediaBrowser(
                context,
                ComponentName(com.uriel.logpose.core.services.LogPoseNotificationListener.SPOTIFY_PACKAGE, "com.spotify.music.MediaBrowserService"),
                callback,
                null
            )
            browser = mediaBrowser
            mediaBrowser.connect()
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear MediaBrowser: ${e.message}")
            onRejected()
        }
    }

    fun disconnect() {
        try {
            browser?.disconnect()
        } catch (e: Exception) {}
        browser = null
    }
}
