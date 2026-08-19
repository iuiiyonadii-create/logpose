package com.uriel.logpose.thamis.developer_platform.sdk

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 25.21 — THAMIS DEVELOPER PLATFORM
 * FASE 1: THAMIS SDK CORE
 */
object ThamisSDK {

    private var isInitialized = false

    fun initialize() {
        if (!isInitialized) {
            isInitialized = true
            LogPoseLogger.i("ThamisSDK: Inicializado correctamente")
        }
    }

    fun registerExtension(extensionId: String) {
        if (!isInitialized) throw IllegalStateException("SDK must be initialized first")
        LogPoseLogger.i("ThamisSDK: Extensión registrada: $extensionId")
    }
}
