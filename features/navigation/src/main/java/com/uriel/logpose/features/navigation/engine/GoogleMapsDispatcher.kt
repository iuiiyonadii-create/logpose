package com.uriel.logpose.features.navigation.engine

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * GoogleMapsDispatcher: Lanzador de navegación manos libres para Google Maps.
 * Recibe destinos en carne viva (ej. "ypf chascomús") y abre la ruta directa en Maps.
 */
object GoogleMapsDispatcher {

    fun lanzarNavegacion(context: Context, destination: String) {
        if (destination.isBlank()) {
            LogPoseLogger.w("GoogleMapsDispatcher: Destino vacío. Abortando navegación.")
            return
        }

        try {
            // v88.1: Limpieza de URI Staff - Si ya es una URI (geo: o google.navigation:), la usamos directo
            val finalUri = when {
                destination.startsWith("google.navigation:") || destination.startsWith("geo:") -> {
                    Uri.parse(destination)
                }
                else -> {
                    Uri.parse("google.navigation:q=${Uri.encode(destination)}")
                }
            }
            
            LogPoseLogger.i("GoogleMapsDispatcher: Lanzando navegación hacia '$destination'")
            val mapIntent = Intent(Intent.ACTION_VIEW, finalUri).apply {
                setPackage("com.google.android.apps.maps")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            LogPoseLogger.e("GoogleMapsDispatcher: Maps no instalado o falló. Probando navegador fallback: ${e.message}")
            try {
                val fallbackUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${Uri.encode(destination)}")
                val browserIntent = Intent(Intent.ACTION_VIEW, fallbackUri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
            } catch (fallbackErr: Exception) {
                LogPoseLogger.e("GoogleMapsDispatcher: Error crítico al lanzar navegación fallback: ${fallbackErr.message}")
            }
        }
    }
}
