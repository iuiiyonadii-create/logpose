package com.uriel.logpose.core.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.uriel.logpose.R
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * LogPoseHudService: Gestiona la interfaz flotante (HUD) sobre otras aplicaciones.
 */
class LogPoseHudService : Service() {

    private var windowManager: WindowManager? = null
    private var hudView: View? = null
    private var statusText: TextView? = null
    private var debugText: TextView? = null

    companion object {
        private var instance: LogPoseHudService? = null
        
        fun updateStatus(text: String) {
            instance?.statusText?.post {
                instance?.statusText?.text = text
                
                // v1.1: Feedback visual de Warning (Misión #025)
                if (text.contains("⚠️") || text.contains("RECUPERANDO")) {
                    instance?.statusText?.setTextColor(android.graphics.Color.YELLOW)
                } else {
                    instance?.statusText?.setTextColor(android.graphics.Color.GREEN)
                }
            }
        }

        fun showDebug(text: String?) {
            instance?.debugText?.post {
                if (text.isNullOrBlank()) {
                    instance?.debugText?.visibility = View.GONE
                } else {
                    instance?.debugText?.visibility = View.VISIBLE
                    instance?.debugText?.text = "🛠 $text"
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createHud()
    }

    private fun createHud() {
        val inflater = LayoutInflater.from(this)
        // Usaremos un layout simple por ahora, luego podemos migrar a Compose
        hudView = inflater.inflate(R.layout.layout_hud_overlay, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params.y = 100

        statusText = hudView?.findViewById(R.id.hud_status_text)
        debugText = hudView?.findViewById(R.id.hud_debug_text)
        
        windowManager?.addView(hudView, params)
        LogPoseLogger.i("HUD: Interfaz flotante desplegada.")
    }

    override fun onDestroy() {
        super.onDestroy()
        hudView?.let { windowManager?.removeView(it) }
        instance = null
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
