package com.uriel.logpose.thamis.developer_platform.api

import com.uriel.logpose.core.compat.core.Command

/**
 * FASE 25.21 — THAMIS DEVELOPER PLATFORM
 * FASE 5: PUBLIC API SYSTEM
 */
interface PublicAPI {
    
    /**
     * Permite a las extensiones solicitar una acción.
     * Pasa siempre por el Safety Engine.
     */
    fun requestAction(command: Command)

    /**
     * Permite consultar el estado de la sesión (solo datos no privados).
     */
    fun getSessionStatus(): String
}
