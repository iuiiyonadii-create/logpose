package com.uriel.logpose.features.settings

import com.uriel.logpose.domain.settings.SettingsStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Orquesta [SettingsSession] (estado en memoria) y [SettingsStore]
 * (persistencia) y produce [SettingsState]/[SettingsCoreEvent] para las
 * capas superiores, siguiendo el mismo patron que
 * `features.notifications.NotificationRepository`.
 *
 * Depende de la interfaz [SettingsStore] (no de
 * `data.preferences.SettingsPreferences` directamente), lo que permite
 * testear toda la logica de esta clase con JUnit plano usando un fake en
 * memoria, sin necesidad de un Context de Android ni de Robolectric.
 */
class SettingsRepository(
    private val session: SettingsSession,
    private val store: SettingsStore
) {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<SettingsCoreEvent>(extraBufferCapacity = 16)
    val events: SharedFlow<SettingsCoreEvent> = _events.asSharedFlow()

    /**
     * Carga en [session] todo lo que ya estaba persistido en [store]. Debe
     * llamarse una unica vez al arrancar, antes de que cualquier
     * ViewModel/Compose lea [state] (ver [SettingsManager.start]).
     */
    fun hydrate() {
        val strings = mutableMapOf<String, String>()
        val booleans = mutableMapOf<String, Boolean>()
        val ints = mutableMapOf<String, Int>()

        store.snapshot().forEach { (key, value) ->
            when (value) {
                is String -> strings[key] = value
                is Boolean -> booleans[key] = value
                is Int -> ints[key] = value
                else -> Unit // Float/Long/Set<String>: fuera de alcance de Settings Core v1
            }
        }

        session.replaceAll(strings = strings, booleans = booleans, ints = ints)
        refresh()
        _events.tryEmit(SettingsCoreEvent.Loaded)
    }

    fun setString(key: String, value: String) {
        store.putString(key, value)
        session.setString(key, value)
        refresh()
        _events.tryEmit(SettingsCoreEvent.Changed(key))
    }

    fun setBoolean(key: String, value: Boolean) {
        store.putBoolean(key, value)
        session.setBoolean(key, value)
        refresh()
        _events.tryEmit(SettingsCoreEvent.Changed(key))
    }

    fun setInt(key: String, value: Int) {
        store.putInt(key, value)
        session.setInt(key, value)
        refresh()
        _events.tryEmit(SettingsCoreEvent.Changed(key))
    }

    fun remove(key: String) {
        store.remove(key)
        session.remove(key)
        refresh()
        _events.tryEmit(SettingsCoreEvent.Removed(key))
    }

    fun getString(key: String, default: String? = null): String? =
        session.getString(key, default)

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        session.getBoolean(key, default)

    fun getInt(key: String, default: Int = 0): Int =
        session.getInt(key, default)

    fun clear() {
        store.clear()
        session.replaceAll()
        refresh()
        _events.tryEmit(SettingsCoreEvent.Cleared)
    }

    /**
     * Recalcula [state] a partir de la configuracion actual de [session].
     * Debe llamarse tras cualquier mutacion para que la UI vea el cambio
     * de inmediato (mismo criterio que
     * `NotificationRepository.refresh()`).
     */
    fun refresh() {
        val config = session.config.value
        _state.value = SettingsState(
            strings = config.strings,
            booleans = config.booleans,
            ints = config.ints,
            isLoaded = true
        )
    }
}
