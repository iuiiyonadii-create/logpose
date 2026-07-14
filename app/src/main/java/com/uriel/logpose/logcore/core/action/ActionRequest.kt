package com.uriel.logpose.logcore.core.action

import com.uriel.logpose.logcore.core.capability.Capability

data class ActionRequest(

    val capability: Capability,

    val timestamp: Long = System.currentTimeMillis()

)