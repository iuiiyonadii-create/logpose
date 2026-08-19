package com.uriel.logpose.thamis.release.beta

import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.thamis.release.versioning.VersionManager

/**
 * Gestor principal de la beta pública.
 */
object PublicBetaManager {

    fun initializeBeta() {
        LogPoseLogger.i("THAMIS_RELEASE: Iniciando motor de Beta Pública.")
        VersionManager.registerNewVersion("v1.0.0-beta", listOf(
            "Unificación de cerebro cognitivo",
            "Soporte fonético rioplatense mejorado",
            "Auto-recuperación de Bluetooth",
            "Resumen de notificaciones inteligente"
        ))
    }

    fun isReadyForDistribution(): Boolean {
        // En v1.0 comprobamos si la versión actual es estable
        return VersionManager.getCurrentVersion().contains("beta")
    }
}
