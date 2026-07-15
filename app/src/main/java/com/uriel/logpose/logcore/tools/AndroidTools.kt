package com.uriel.logpose.logcore.tools

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper

/**
 * Utilidades generales dependientes del framework de Android (no de UI/Activities/Fragments/Compose).
 * Requiere `android.jar` / SDK de Android para compilar (ver README, sección "Dependencias").
 */
object AndroidTools {

    private val mainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    /** Indica si el código se está ejecutando en el hilo principal de Android. */
    fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()

    /** Ejecuta [block] en el hilo principal de Android. */
    fun runOnMainThread(block: () -> Unit) {
        if (isMainThread()) block() else mainHandler.post(block)
    }

    /** Ejecuta [block] en el hilo principal luego de [delayMillis] milisegundos. */
    fun runOnMainThreadDelayed(delayMillis: Long, block: () -> Unit) {
        mainHandler.postDelayed(block, delayMillis)
    }

    /** Devuelve el `versionName` de la app, o `null` si no se puede resolver. */
    fun appVersionName(context: Context): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    /** Devuelve el código de versión de la app, o `-1` si no se puede resolver. */
    fun appVersionCode(context: Context): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            -1L
        }
    }

    /** Indica si la versión de SDK del dispositivo es al menos [sdkVersion]. */
    fun isAtLeast(sdkVersion: Int): Boolean = Build.VERSION.SDK_INT >= sdkVersion
}
