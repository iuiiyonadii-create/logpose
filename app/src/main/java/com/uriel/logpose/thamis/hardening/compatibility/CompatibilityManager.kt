package com.uriel.logpose.thamis.hardening.compatibility

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Valida la compatibilidad del sistema con diferentes entornos de hardware y OS.
 */
object CompatibilityManager {

    fun validateEnvironment(androidVersion: Int, deviceModel: String): Boolean {
        LogPoseLogger.i("THAMIS_COMPATIBILITY: Validando entorno en $deviceModel (Android $androidVersion)")
        
        // Regla: THAMIS requiere Android 8.0+ para una latencia de audio aceptable
        if (androidVersion < 26) {
            LogPoseLogger.e("THAMIS_COMPATIBILITY: Versión de Android no soportada para perfiles de alto rendimiento.")
            return false
        }
        
        return true
    }

    fun checkBluetoothStack(profile: String): Boolean {
        // Soporte para perfiles SCO y A2DP avanzados
        return profile.contains("HFP") || profile.contains("HSP")
    }
}
