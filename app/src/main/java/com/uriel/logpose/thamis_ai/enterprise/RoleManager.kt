package com.uriel.logpose.thamis_ai.enterprise

/**
 * Defines and enforces roles (ADMIN, MANAGER, DRIVER, TECHNICIAN).
 */
class RoleManager {
    enum class Role { ADMIN, MANAGER, DRIVER, TECHNICIAN }
    
    fun getRole(userId: String): Role = Role.DRIVER
}
