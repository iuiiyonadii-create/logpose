package com.uriel.logpose.thamis.notification.memory

import com.uriel.logpose.thamis.notification.model.NotificationCategory

/**
 * Memoria inmediata de notificaciones para aprender relevancia.
 */
object NotificationMemory {

    private val appFrequencies = mutableMapOf<String, Int>()
    private val categoryHistory = mutableListOf<NotificationCategory>()

    fun record(app: String, category: NotificationCategory) {
        val current = appFrequencies[app] ?: 0
        appFrequencies[app] = current + 1
        categoryHistory.add(category)
        if (categoryHistory.size > 100) categoryHistory.removeAt(0)
    }

    fun isFrequentApp(app: String): Boolean {
        return (appFrequencies[app] ?: 0) > 10
    }

    fun clear() {
        appFrequencies.clear()
        categoryHistory.clear()
    }
}
