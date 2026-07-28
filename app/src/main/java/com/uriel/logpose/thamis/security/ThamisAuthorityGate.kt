package com.uriel.logpose.thamis.security

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Gestiona la autoridad real de THAMIS sobre los diferentes dominios del sistema.
 * Solo permite la ejecución si el dominio está autorizado y las condiciones de seguridad se cumplen.
 */
object ThamisAuthorityGate {

    private const val TAG = "THAMIS_AUTHORITY"

    enum class Domain {
        MULTIMEDIA,
        COMMUNICATION,
        NAVIGATION,
        NOTIFICATION
    }

    private val authorizedDomains = mutableMapOf(
        Domain.MULTIMEDIA to true,
        Domain.COMMUNICATION to true, // Activado en Fase 16
        Domain.NAVIGATION to true, // Activado en Fase 15
        Domain.NOTIFICATION to false
    )

    fun canExecute(domain: Domain): Boolean {
        val isAuthorized = authorizedDomains[domain] ?: false
        if (!isAuthorized) {
            LogPoseLogger.w("$TAG: Domain $domain is currently DENIED.")
        } else {
            LogPoseLogger.i("$TAG: Domain $domain is GRANTED.")
        }
        return isAuthorized
    }
}
