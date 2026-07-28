package com.uriel.logpose.thamis.enterprise

/**
 * FASE 25.22 — THAMIS ENTERPRISE ARCHITECTURE
 * FASE 5: ROLE MANAGEMENT
 */
enum class EnterpriseRole {
    ADMIN,
    MANAGER,
    DRIVER,
    TECHNICIAN
}

object RoleManager {
    
    fun getPermissions(role: EnterpriseRole): List<String> {
        return when (role) {
            EnterpriseRole.ADMIN -> listOf("all")
            EnterpriseRole.DRIVER -> listOf("read_safety", "execute_commands")
            else -> listOf("read_basic")
        }
    }
}
