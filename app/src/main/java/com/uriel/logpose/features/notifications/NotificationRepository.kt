package com.uriel.logpose.features.notifications

import com.uriel.logpose.domain.models.NotificationContent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Orquesta [NotificationSession] (reglas), [NotificationHistoryStore]
 * (almacenamiento) y produce [NotificationState]/[NotificationCoreEvent]
 * para las capas superiores. Es deliberadamente independiente de Android:
 * solo trabaja con [NotificationContent] (dominio), nunca con
 * StatusBarNotification directamente, para poder testearse sin framework
 * Android (ver features/notifications/README.md).
 */
class NotificationRepository(
    private val session: NotificationSession,
    private val history: NotificationHistoryStore = NotificationHistoryStore()
) {

    private val _state = MutableStateFlow(NotificationState())
    val state: StateFlow<NotificationState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<NotificationCoreEvent>(extraBufferCapacity = 16)
    val events: SharedFlow<NotificationCoreEvent> = _events.asSharedFlow()

    /**
     * Procesa una notificacion recien publicada. No hace nada si la sesion
     * la rechaza (paquete no permitido o categoria deshabilitada) — esas
     * notificaciones nunca llegan a existir en el historial de Notification
     * Core, igual que ProbeGuard hace para LogProbe, pero con reglas
     * completamente separadas (Decision 3).
     */
    fun onPosted(content: NotificationContent) {
        if (!session.isAllowed(content.packageName)) return

        val item = NotificationItem.from(content)
        if (!session.isCategoryAllowed(item.category)) return

        history.upsert(item)
        refresh()
        _events.tryEmit(NotificationCoreEvent.Posted(item))
    }

    fun onRemoved(content: NotificationContent) {
        history.markRemoved(content.key)
        refresh()
        _events.tryEmit(NotificationCoreEvent.Removed(content.key))
    }

    fun markRead(id: String) {
        history.markRead(id)
        refresh()
    }

    fun markAllRead() {
        history.markAllRead()
        refresh()
    }

    fun clear() {
        history.clear()
        refresh()
        _events.tryEmit(NotificationCoreEvent.HistoryCleared)
    }

    fun byPackage(packageName: String): List<NotificationItem> = history.byPackage(packageName)

    /**
     * Recalcula [state] a partir del historial actual y de la
     * configuracion vigente de [session]. Debe llamarse tanto tras mutar
     * el historial como tras cualquier cambio de configuracion de sesion
     * (whitelist, blacklist, modo conduccion) para que la UI vea el
     * cambio de inmediato.
     */
    fun refresh() {
        val config = session.config.value
        _state.value = NotificationState(
            items = history.all(),
            unreadCount = history.unreadCount(),
            enabled = config.enabled,
            drivingModeEnabled = config.drivingModeEnabled,
            whitelist = config.filter.whitelist,
            blacklist = config.filter.blacklist
        )
    }
}
