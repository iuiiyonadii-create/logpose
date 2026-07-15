package com.uriel.logpose.logcore.core.action

import com.uriel.logpose.logcore.core.capability.Capability

/**
 * Solicitud lista para ser ejecutada por LogCore.
 */
data class ActionRequest(

    val capability: Capability,

    val parameters: List<Parameter> = emptyList()

)