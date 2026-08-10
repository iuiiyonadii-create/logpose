package com.uriel.logpose.thamis_ai.global

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Logs regional configuration changes and global system events.
 */
class GlobalLogger {
    fun logRegionChange(old: String, new: String) {
        LogPoseLogger.d("Global", "Region switched from $old to $new")
    }
}
