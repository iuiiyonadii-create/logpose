package com.uriel.logpose.features.settings

import com.uriel.logpose.domain.settings.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Fachada oficial de Settings Core.
 */
class SettingsManager(
    store: SettingsStore,
    private val session: SettingsSession = SettingsSession(),
    private val repository: SettingsRepository = SettingsRepository(session, store)
) {

    val state: StateFlow<SettingsState> = repository.state

    val events: SharedFlow<SettingsCoreEvent> = repository.events

    /** 
     * Hidrata la sesion desde el almacenamiento persistente. 
     * Mejorado: Se ejecuta en segundo plano para no bloquear el arranque.
     */
    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.hydrate()
        }
    }

    fun getString(key: String, default: String? = null): String? =
        repository.getString(key, default)

    fun setString(key: String, value: String) {
        repository.setString(key, value)
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        repository.getBoolean(key, default)

    fun setBoolean(key: String, value: Boolean) {
        repository.setBoolean(key, value)
    }

    fun getInt(key: String, default: Int = 0): Int =
        repository.getInt(key, default)

    fun setInt(key: String, value: Int) {
        repository.setInt(key, value)
    }

    fun remove(key: String) {
        repository.remove(key)
    }

    fun clearAll() {
        repository.clear()
    }
}
