package com.uriel.logpose.thamis_ai.enterprise

import android.util.Log

/**
 * Logs administrative events and organization-level changes.
 */
class EnterpriseLogger {
    fun logAdminAction(adminId: String, action: String) {
        Log.d("Enterprise", "Admin $adminId performed $action")
    }
}
