package com.uriel.logpose.features.notifications

import com.uriel.logpose.domain.models.NotificationCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Configuracion mutable y en memoria de la sesion actual de Notification
 * Core: whitelist, blacklist, modo conduccion y habilitado/deshabilitado
 * general. Completamente independiente de logprobe.common.ProbeSession
 * (Decision 3 y 4, Sprint Notification Core): no la extiende, no la
 * reutiliza, no comparte estado con ella.
 *
 * A diferencia de ProbeSession (inmutable, se recrea por cada sesion de
 * diagnostico), NotificationSession vive durante todo el ciclo de vida de
 * la app y se actualiza en caliente: el usuario puede cambiar la whitelist
 * o activar modo conduccion sin perder el historial acumulado.
 */
class NotificationSession(
    channelManager: NotificationChannelManager = NotificationChannelManager()
) {

    val channels: NotificationChannelManager = channelManager

    private val _config = MutableStateFlow(NotificationSessionConfig())
    val config: StateFlow<NotificationSessionConfig> = _config.asStateFlow()

    fun setEnabled(enabled: Boolean) {
        _config.value = _config.value.copy(enabled = enabled)
    }

    fun setDrivingMode(enabled: Boolean) {
        _config.value = _config.value.copy(drivingModeEnabled = enabled)
    }

    fun setWhitelist(packages: Set<String>) {
        _config.value = _config.value.copy(filter = _config.value.filter.withWhitelist(packages))
    }

    fun setBlacklist(packages: Set<String>) {
        _config.value = _config.value.copy(filter = _config.value.filter.withBlacklist(packages))
    }

    /**
     * Decide si una notificacion de [packageName] deberia procesarse.
     * En modo conduccion, ademas del filtro normal, solo se permiten
     * categorias explicitamente habilitadas en [channels] (por defecto
     * todas, hasta que el usuario restrinja alguna).
     */
    fun isAllowed(packageName: String): Boolean {
        val current = _config.value
        if (!current.enabled) return false
        return current.filter.isAllowed(packageName)
    }

    fun isCategoryAllowed(category: NotificationCategory): Boolean =
        channels.isEnabled(category)
}

data class NotificationSessionConfig(
    val enabled: Boolean = true,
    val drivingModeEnabled: Boolean = false,
    val filter: NotificationFilter = NotificationFilter()
)
