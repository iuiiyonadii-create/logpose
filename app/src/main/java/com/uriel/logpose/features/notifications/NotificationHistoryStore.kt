package com.uriel.logpose.features.notifications

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * Historial en memoria de Notification Core. Acotado (bounded) por diseno:
 * LogPose prioriza dispositivos de gama baja (PROJECT_CONTEXT.md), asi que
 * este historial nunca crece sin limite; al superar [maxSize] descarta las
 * entradas mas antiguas primero.
 *
 * No es logprobe.notification.NotificationHistory: esa clase es una vista
 * de solo lectura sobre ProbeTimeline (LogProbe). Esta es la unica fuente
 * de verdad del historial de Notification Core, sin relacion con LogProbe
 * (Decision 3).
 */
class NotificationHistoryStore(private val maxSize: Int = 200) {

    private val lock = ReentrantReadWriteLock()
    private val items = LinkedHashMap<String, NotificationItem>()

    fun upsert(item: NotificationItem): NotificationItem = lock.write {
        items[item.id] = item
        while (items.size > maxSize) {
            val oldestKey = items.keys.first()
            items.remove(oldestKey)
        }
        item
    }

    fun markRemoved(id: String) = lock.write {
        items.remove(id)
    }

    fun markRead(id: String): NotificationItem? = lock.write {
        val current = items[id] ?: return@write null
        val updated = current.copy(isRead = true)
        items[id] = updated
        updated
    }

    fun markAllRead() = lock.write {
        items.replaceAll { _, item -> item.copy(isRead = true) }
    }

    fun all(): List<NotificationItem> = lock.read {
        items.values.sortedByDescending { it.postedAtMillis }
    }

    fun byPackage(packageName: String): List<NotificationItem> = lock.read {
        items.values.filter { it.packageName == packageName }
            .sortedByDescending { it.postedAtMillis }
    }

    fun unreadCount(): Int = lock.read {
        items.values.count { !it.isRead }
    }

    fun clear() = lock.write {
        items.clear()
    }
}
