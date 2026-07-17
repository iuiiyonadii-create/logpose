package com.uriel.logpose.features.settings

import com.uriel.logpose.domain.settings.SettingsStore
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Fachada oficial de Settings Core (Sprint 06). Es el unico punto de
 * entrada que ViewModels/Compose deberian usar. Por debajo coordina
 * [SettingsSession] (estado en memoria) y [SettingsRepository]
 * (persistencia + estado/eventos), respetando el flujo oficial de
 * PROJECT_CONTEXT.md:
 *
 * Compose -> ViewModel -> SettingsManager -> SettingsRepository ->
 * SettingsStore -> Android SharedPreferences
 *
 * Igual que `NotificationCoreManager`, no depende de Bluetooth, Voice,
 * Music, Calls, THAMIS ni LogPoseEngine (fuera de alcance de este Sprint,
 * ver Sprint 06 / NO MODIFICAR); expone [state] y [events] para que esos
 * modulos se integren a futuro sin que Settings Core dependa de ellos.
 *
 * Este Sprint construye unicamente la infraestructura: [SettingsManager]
 * expone lectura/escritura generica por clave (String/Boolean/Int), sin
 * ninguna clave de ajuste especifica de negocio.
 */
class SettingsManager(
    store: SettingsStore,
    private val session: SettingsSession = SettingsSession(),
    private val repository: SettingsRepository = SettingsRepository(session, store)
) {

    val state: StateFlow<SettingsState> = repository.state

    val events: SharedFlow<SettingsCoreEvent> = repository.events

    /** Hidrata la sesion desde el almacenamiento persistente. Llamar una vez, desde AppContainer. */
    fun start() {
        repository.hydrate()
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
