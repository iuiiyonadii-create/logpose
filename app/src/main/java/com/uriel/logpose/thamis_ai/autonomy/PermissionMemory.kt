package com.uriel.logpose.thamis_ai.autonomy

/**
 * Remembers user approvals for specific automations.
 */
class PermissionMemory {
    private val allowedAutomations = mutableSetOf<String>()

    fun grant(action: String) { allowedAutomations.add(action) }
    fun isAllowed(action: String) = allowedAutomations.contains(action)
}
