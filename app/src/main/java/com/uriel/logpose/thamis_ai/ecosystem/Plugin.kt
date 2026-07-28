package com.uriel.logpose.thamis_ai.ecosystem

/**
 * Definition of a THAMIS extension.
 */
interface Plugin {
    val id: String
    val name: String
    val version: String
    val requiredPermissions: List<String>

    fun onInitialize()
    fun onEvent(event: String, data: Any?)
}
