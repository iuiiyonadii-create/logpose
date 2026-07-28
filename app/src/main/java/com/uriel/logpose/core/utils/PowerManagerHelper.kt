package com.uriel.logpose.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Ayuda a gestionar la supervivencia del proceso en sistemas agresivos como HyperOS/MIUI.
 */
object PowerManagerHelper {

    private var wakeLock: PowerManager.WakeLock? = null

    /**
     * Adquiere un PARTIAL_WAKE_LOCK para asegurar que la CPU no se duerma durante el viaje.
     */
    fun acquireWakeLock(context: Context) {
        if (wakeLock != null) return
        
        try {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LogPose:TripWakeLock").apply {
                acquire()
            }
            LogPoseLogger.i("Power: WakeLock adquirido para el viaje.")
        } catch (e: Exception) {
            LogPoseLogger.e("Power: Error al adquirir WakeLock: ${e.message}")
        }
    }

    /**
     * Libera el WakeLock al finalizar el viaje.
     */
    fun releaseWakeLock() {
        try {
            wakeLock?.let {
                if (it.isHeld) it.release()
            }
            wakeLock = null
            LogPoseLogger.i("Power: WakeLock liberated.")
        } catch (e: Exception) {
            LogPoseLogger.e("Power: Error al liberar WakeLock: ${e.message}")
        }
    }

    /**
     * Verifica si la app está ignorando las optimizaciones de batería.
     */
    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            pm.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }
    }

    /**
     * Detecta si el dispositivo es Xiaomi (MIUI/HyperOS).
     */
    fun isXiaomiDevice(): Boolean {
        return Build.MANUFACTURER.lowercase().contains("xiaomi")
    }

    /**
     * Obtiene el Intent para abrir la configuración de Inicio Automático en Xiaomi.
     * Nota: Puede fallar si Xiaomi cambia el package/activity en versiones futuras.
     */
    fun getXiaomiAutostartIntent(): Intent {
        return Intent().apply {
            setClassName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    /**
     * Solicita al usuario ignorar las optimizaciones de batería.
     */
    fun requestIgnoreBatteryOptimizations(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        if (isIgnoringBatteryOptimizations(context)) return

        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            LogPoseLogger.i("Power: Solicitando permiso para ignorar optimizaciones de batería.")
        } catch (e: Exception) {
            LogPoseLogger.e("Power: Error al solicitar ignorar optimizaciones: ${e.message}")
        }
    }
}
