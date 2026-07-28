package com.uriel.logpose.core.app

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager

/**
 * TrampolineActivity v3.0: El bypass universal.
 * Capaz de lanzar CUALQUIER aplicación o búsqueda de Spotify sobre el Keyguard.
 */
class TrampolineActivity : Activity() {

    companion object {
        const val EXTRA_ARTIST_QUERY = "artist_query"
        const val EXTRA_TARGET_INTENT = "TARGET_INTENT"
        private const val TAG = "LogPoseTrampoline"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Forzar visibilidad sobre bloqueo (Indispensable para HyperOS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }

        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager.requestDismissKeyguard(this, null)
        }

        // 2. Lógica de Ruteo Inteligente
        val targetIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TARGET_INTENT, Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_TARGET_INTENT)
        }

        try {
            if (targetIntent != null) {
                // CASO A: Es una App específica (WhatsApp, Mapas, etc.)
                startActivity(targetIntent)
                Log.i(TAG, "🚀 Lanzando aplicación destino: ${targetIntent.`package`}")
            } else {
                // CASO B: Búsqueda musical AGRESIVA para Spotify
                val artistQuery = intent.getStringExtra(EXTRA_ARTIST_QUERY) ?: ""
                Log.i(TAG, "🎯 Ejecutando Martillo para: $artistQuery")

                // Intent 1: El estándar de MediaStore (Suele disparar reproducción automática)
                val searchIntent = Intent(android.provider.MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
                    `package` = "com.spotify.music"
                    putExtra(android.provider.MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/artist")
                    putExtra(android.provider.MediaStore.EXTRA_MEDIA_ARTIST, artistQuery)
                    putExtra(android.app.SearchManager.QUERY, artistQuery)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                
                try {
                    startActivity(searchIntent)
                    
                    // Segundo golpe: Broadcast de Play después de 1.5s por si Spotify se quedó "dormido" en la búsqueda
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        val playIntent = Intent("com.spotify.music.musicservicecommand.ACTION_CMD")
                        playIntent.putExtra("command", "play")
                        playIntent.`package` = "com.spotify.music"
                        sendBroadcast(playIntent)
                        Log.d(TAG, "🔨 Segundo golpe: Comando PLAY enviado.")
                    }, 1500)

                } catch (e: Exception) {
                    val backupUri = "spotify:search:${android.net.Uri.encode(artistQuery)}"
                    startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(backupUri)).apply {
                        `package` = "com.spotify.music"
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Fallo en el salto: ${e.message}")
        }

        // SINCRO CLAUDE: Pequeño delay antes de cerrar para asegurar el Intent en HyperOS
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ finish() }, 500)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
