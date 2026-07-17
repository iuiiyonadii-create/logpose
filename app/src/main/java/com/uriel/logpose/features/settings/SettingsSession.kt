package com.uriel.logpose.features.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Configuracion mutable y en memoria de la sesion actual de Settings Core,
 * siguiendo el mismo patron que `features.notifications.NotificationSession`
 * (ver features/notifications/NotificationSession.kt): vive durante todo el
 * ciclo de vida de la app y se actualiza en caliente, para que la UI
 * reaccione de inmediato sin esperar una lectura a disco.
 *
 * [SettingsSession] es deliberadamente independiente de Android: solo
 * trabaja con tipos de Kotlin puro, nunca con SharedPreferences
 * directamente (esa es responsabilidad de
 * [com.uriel.logpose.domain.settings.SettingsStore]), para poder testearse
 * con JUnit plano.
 */
class SettingsSession {

    private val _config = MutableStateFlow(SettingsSessionConfig())
    val config: StateFlow<SettingsSessionConfig> = _config.asStateFlow()

    fun setString(key: String, value: String) {
        _config.value = _config.value.copy(
            strings = _config.value.strings + (key to value)
        )
    }

    fun setBoolean(key: String, value: Boolean) {
        _config.value = _config.value.copy(
            booleans = _config.value.booleans + (key to value)
        )
    }

    fun setInt(key: String, value: Int) {
        _config.value = _config.value.copy(
            ints = _config.value.ints + (key to value)
        )
    }

    fun remove(key: String) {
        val current = _config.value
        _config.value = current.copy(
            strings = current.strings - key,
            booleans = current.booleans - key,
            ints = current.ints - key
        )
    }

    fun getString(key: String, default: String? = null): String? =
        _config.value.strings[key] ?: default

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        _config.value.booleans[key] ?: default

    fun getInt(key: String, default: Int = 0): Int =
        _config.value.ints[key] ?: default

    /**
     * Reemplaza toda la configuracion en memoria de una sola vez. Pensado
     * para la hidratacion inicial desde [com.uriel.logpose.domain.settings.SettingsStore]
     * ([SettingsRepository.hydrate]) y para [SettingsRepository.clear].
     */
    fun replaceAll(
        strings: Map<String, String> = emptyMap(),
        booleans: Map<String, Boolean> = emptyMap(),
        ints: Map<String, Int> = emptyMap()
    ) {
        _config.value = SettingsSessionConfig(
            strings = strings,
            booleans = booleans,
            ints = ints
        )
    }
}

data class SettingsSessionConfig(
    val strings: Map<String, String> = emptyMap(),
    val booleans: Map<String, Boolean> = emptyMap(),
    val ints: Map<String, Int> = emptyMap()
)
