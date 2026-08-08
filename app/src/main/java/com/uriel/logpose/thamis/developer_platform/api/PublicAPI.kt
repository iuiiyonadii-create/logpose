package com.uriel.logpose.thamis.developer_platform.api

import com.thamis.lab.core.contracts.command.LogPoseCommand

/**
 * FASE 25.21 — THAMIS DEVELOPER PLATFORM
 * FASE 5: PUBLIC API SYSTEM
 */
interface PublicAPI {
    
    /**
     * Permite a las extensiones solicitar una acción.
     * Pasa siempre por el Safety Engine.
     */
    fun requestAction(command: LogPoseCommand)

    /**
     * Permite consultar el estado de la sesión (solo datos no privados).
     */
    fun getSessionStatus(): String
}
