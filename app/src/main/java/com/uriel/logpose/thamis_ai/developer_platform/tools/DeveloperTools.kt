package com.uriel.logpose.thamis_ai.developer_platform.tools

/**
 * Common utilities for extension developers.
 */
object DeveloperTools {
    fun formatEvent(type: String, data: Any): String {
        return "Event: $type - Data: $data"
    }
}
