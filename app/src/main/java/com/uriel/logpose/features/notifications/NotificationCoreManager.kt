package com.uriel.logpose.features.notifications

import com.uriel.logpose.domain.models.NotificationCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

import com.uriel.logpose.features.navigation.NavigationReader

/**
 * Fachada oficial de Notification Core.
 */
class NotificationCoreManager(
    private val session: NotificationSession = NotificationSession(),
    private val repository: NotificationRepository = NotificationRepository(session)
) {

    private val receiver = NotificationReceiver(repository)
    private val scope = CoroutineScope(Dispatchers.Main)

    val state: StateFlow<NotificationState> = repository.state

    val events: SharedFlow<NotificationCoreEvent> = repository.events

    init {
        events.onEach { event ->
            if (event is NotificationCoreEvent.Posted) {
                when (event.item.category) {
                    NotificationCategory.DELIVERY -> NotificationReader.announce(event.item)
                    NotificationCategory.NAVIGATION -> NavigationReader.read(event.item)
                    else -> {}
                }
            }
        }.launchIn(scope)
    }

    /** Empieza a escuchar notificaciones. Llamar una vez, desde AppContainer. */
    fun start() {
        repository.refresh()
        receiver.attach()
    }

    /** Detiene la escucha. Pensado para tests y para un futuro ciclo de vida de proceso. */
    fun stop() {
        receiver.detach()
    }

    fun setEnabled(enabled: Boolean) {
        session.setEnabled(enabled)
        repository.refresh()
    }

    fun setDrivingMode(enabled: Boolean) {
        session.setDrivingMode(enabled)
        repository.refresh()
    }

    fun setWhitelist(packages: Set<String>) {
        session.setWhitelist(packages)
        repository.refresh()
    }

    fun setBlacklist(packages: Set<String>) {
        session.setBlacklist(packages)
        repository.refresh()
    }

    fun setCategoryEnabled(category: NotificationCategory, enabled: Boolean) {
        session.channels.setEnabled(category, enabled)
        repository.refresh()
    }

    fun markAsRead(id: String) {
        repository.markRead(id)
    }

    fun markAllAsRead() {
        repository.markAllRead()
    }

    fun clearHistory() {
        repository.clear()
    }

    fun historyForPackage(packageName: String): List<NotificationItem> =
        repository.byPackage(packageName)
}
