package com.uriel.logpose.thamis_ai.global

import android.util.Log

/**
 * Logs regional configuration changes and global system events.
 */
class GlobalLogger {
    fun logRegionChange(old: String, new: String) {
        Log.d("Global", "Region switched from $old to $new")
    }
}
