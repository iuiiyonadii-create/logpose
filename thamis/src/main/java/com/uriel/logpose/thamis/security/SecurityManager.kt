package com.uriel.logpose.thamis.security

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 26.15 — LOGPOSE THAMIS SECURITY & PRIVACY FOUNDATION
 * FASE 1: SECURITY MANAGER CORE
 */
object SecurityManager {

    private var isPrivacyModeActive = false

    fun setPrivacyMode(enabled: Boolean) {
        isPrivacyModeActive = enabled
        LogPoseLogger.i("SecurityManager: Modo privacidad: ${if (enabled) "ACTIVADO" else "DESACTIVADO"}")
    }

    fun canAccessSensitiveData(): Boolean {
        return !isPrivacyModeActive
    }
}
