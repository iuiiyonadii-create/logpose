package com.uriel.logpose.features.music
 
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
 
/**
 * Tier 2 de warm-up: solo se invoca cuando el bind silencioso no produjo una sesión activa.
 */
class WarmUpTrampolineActivity : Activity() {
 
    companion object {
        private const val TAG = "WarmUpTrampoline"
        private const val AUTO_FINISH_DELAY_MS = 400L
 
        fun launch(context: Context) {
            val intent = Intent(context, WarmUpTrampolineActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }
            context.startActivity(intent)
        }
    }
 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
 
        val launchIntent = packageManager.getLaunchIntentForPackage(
            com.uriel.logpose.core.services.LogPoseNotificationListener.SPOTIFY_PACKAGE
        )
        if (launchIntent == null) {
            Log.w(TAG, "Spotify no está instalado o no expone launch intent")
            finish()
            return
        }
 
        launchIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_NO_ANIMATION or
            Intent.FLAG_ACTIVITY_NO_USER_ACTION
        )
 
        try {
            startActivity(launchIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Fallo al lanzar trampoline de Spotify", e)
        }
 
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
            overridePendingTransition(0, 0)
        }, AUTO_FINISH_DELAY_MS)
    }
}
