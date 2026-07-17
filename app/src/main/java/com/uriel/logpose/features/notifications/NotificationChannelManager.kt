package com.uriel.logpose.features.notifications

import com.uriel.logpose.domain.models.NotificationCategory
import java.util.concurrent.ConcurrentHashMap

/**
 * Administra que [NotificationCategory] estan habilitadas dentro de
 * Notification Core. Complementa a [NotificationFilter] (que decide por
 * paquete): esta clase decide por categoria, p.ej. "no quiero ver
 * notificaciones de MEDIA mientras conduzco, pero si de CALL y MESSAGE".
 *
 * No tiene relacion con android.app.NotificationChannel (los canales que
 * el propio sistema operativo expone en Ajustes); es un concepto de
 * producto interno de LogPose.
 */
class NotificationChannelManager {

    private val disabledCategories = ConcurrentHashMap.newKeySet<NotificationCategory>()

    fun isEnabled(category: NotificationCategory): Boolean =
        category !in disabledCategories

    fun enable(category: NotificationCategory) {
        disabledCategories.remove(category)
    }

    fun disable(category: NotificationCategory) {
        disabledCategories.add(category)
    }

    fun setEnabled(category: NotificationCategory, enabled: Boolean) {
        if (enabled) enable(category) else disable(category)
    }

    fun enabledCategories(): Set<NotificationCategory> =
        NotificationCategory.entries.filterNot { it in disabledCategories }.toSet()

    fun disabledCategoriesSnapshot(): Set<NotificationCategory> =
        disabledCategories.toSet()
}
