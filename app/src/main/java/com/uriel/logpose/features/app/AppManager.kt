package com.uriel.logpose.features.app

import android.content.Context
import android.content.Intent
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * AppManager: Versión 3.0 - TRAMPOLÍN FORZADO
 */
object AppManager {

    private lateinit var context: Context

    private val APP_MAP = mapOf(
        "whatsapp" to "com.whatsapp",
        "spotify" to "com.spotify.music",
        "instagram" to "com.instagram.android",
        "maps" to "com.google.android.apps.maps",
        "google maps" to "com.google.android.apps.maps",
        "gmail" to "com.google.android.gm",
        "google" to "com.android.chrome",
        "chrome" to "com.android.chrome",
        "maps" to "com.google.android.apps.maps",
        "youtube" to "com.google.android.youtube",
        "yt" to "com.google.android.youtube",
        "waze" to "com.waze"
    )

    fun initialize(context: Context) {
        this.context = context.applicationContext
    }

    fun openApp(name: String) {
        val cleanName = name.lowercase().trim()
        val packageName = APP_MAP[cleanName]
        
        LogPoseLogger.i("--- GESTOR DE APERTURA LOGPOSE v3.0 ---")
        
        if (packageName != null) {
            launchViaTrampoline(packageName)
        } else {
            LogPoseLogger.w("Buscando coincidencia parcial para: $cleanName")
            tryLaunchByPartialName(cleanName)
        }
    }

    private fun launchViaTrampoline(packageName: String) {
        val targetIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (targetIntent != null) {
            targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            
            try {
                LogPoseLogger.i(">>> LANZANDO TRAMPOLÍN INVISIBLE PARA: $packageName <<<")
                
                val trampolineIntent = Intent(context, com.uriel.logpose.core.app.TrampolineActivity::class.java).apply {
                    putExtra("TARGET_INTENT", targetIntent)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                }
                
                context.startActivity(trampolineIntent)
            } catch (e: Exception) {
                LogPoseLogger.e("ERROR CRÍTICO EN TRAMPOLÍN: ${e.message}")
                context.startActivity(targetIntent)
            }
        }
    }

    private fun tryLaunchByPartialName(name: String) {
        val pm = context.packageManager
        val apps = pm.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)
        for (app in apps) {
            if (app.loadLabel(pm).toString().lowercase().contains(name)) {
                LogPoseLogger.i("Coincidencia encontrada por nombre: ${app.activityInfo.packageName}")
                launchViaTrampoline(app.activityInfo.packageName)
                return
            }
        }
        LogPoseLogger.e("No se encontró ninguna app llamada: $name")
    }
}
