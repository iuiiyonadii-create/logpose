package com.uriel.logpose.features.notifications

import com.uriel.logpose.domain.models.NotificationCategory
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Fachada oficial de Notification Core (Decision 4, Sprint Notification
 * Core: renombrada desde "NotificationManager" para no colisionar con
 * android.app.NotificationManager, ya en uso en
 * core.notifications.NotificationHelper).
 *
 * Es el unico punto de entrada que ViewModels/Compose deberian usar. Por
 * debajo coordina [NotificationSession] (reglas), [NotificationRepository]
 * (estado/historial) y [NotificationReceiver] (conexion con
 * NotificationProbeService), respetando el flujo oficial:
 *
 * Compose -> ViewModel -> NotificationCoreManager -> NotificationRepository
 * -> NotificationProbeService -> Android NotificationListenerService
 *
 * No depende de Bluetooth, Voice ni THAMIS (requisito explicito del
 * Sprint); expone [state] y [events] para que esos modulos se integren
 * a futuro sin que Notification Core dependa de ellos.
 */
class NotificationCoreManager(
    private val session: NotificationSession = NotificationSession(),
    private val repository: NotificationRepository = NotificationRepository(session)
) {

    private val receiver = NotificationReceiver(repository)

    val state: StateFlow<NotificationState> = repository.state

    val events: SharedFlow<NotificationCoreEvent> = repository.events

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
