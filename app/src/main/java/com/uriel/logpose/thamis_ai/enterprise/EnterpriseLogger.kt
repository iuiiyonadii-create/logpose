package com.uriel.logpose.thamis_ai.enterprise

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Logs administrative events and organization-level changes.
 */
class EnterpriseLogger {
    fun logAdminAction(adminId: String, action: String) {
        LogPoseLogger.d("Enterprise", "Admin $adminId performed $action")
    }
}
