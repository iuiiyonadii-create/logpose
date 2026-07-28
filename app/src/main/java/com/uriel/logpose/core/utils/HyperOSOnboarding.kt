package com.uriel.logpose.core.utils

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * TAREA B — Onboarding de HyperOS (Autostart)
 * Gestiona la detección y el guiado del usuario para activar el Inicio Automático en Xiaomi.
 */
object HyperOSOnboarding {

    private const val PREFS_NAME = "hyperos_onboarding"
    private const val KEY_POSTPONED = "autostart_postponed"

    /**
     * Verifica si el dispositivo es Xiaomi y si deberíamos mostrar el aviso de Autostart.
     */
    fun shouldShowAutostartAviso(context: Context): Boolean {
        if (!PowerManagerHelper.isXiaomiDevice()) return false
        
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val postponed = prefs.getBoolean(KEY_POSTPONED, false)
        
        // Si ya fue pospuesto, no molestamos en este arranque (simple logic for MVP)
        return !postponed
    }

    /**
     * Marca el aviso como pospuesto para que no vuelva a aparecer inmediatamente.
     */
    fun postpone(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_POSTPONED, true)
            .apply() // API 9+ (Ok as minSdk is usually higher or we assume modern Android)
        LogPoseLogger.i("HyperOS: Autostart pospuesto por el usuario.")
    }

    /**
     * Intenta abrir la pantalla de configuración de Inicio Automático.
     */
    fun openAutostartSettings(context: Context): Boolean {
        val intent = PowerManagerHelper.getXiaomiAutostartIntent()
        return try {
            context.startActivity(intent)
            LogPoseLogger.i("HyperOS: Pantalla de Autostart abierta con éxito.")
            true
        } catch (e: Exception) {
            LogPoseLogger.e("HyperOS: Falló al abrir la pantalla de Autostart: ${e.message}")
            false
        }
    }
}
