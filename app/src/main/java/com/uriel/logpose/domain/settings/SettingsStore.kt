package com.uriel.logpose.domain.settings

/**
 * Contrato de persistencia de Settings Core. Vive en `domain.settings` (no en
 * `features.settings` ni en `data.preferences`) por la misma razon que
 * `NotificationContent` vive en `domain.models` (ver
 * features/notifications/ARCHITECTURE.md): tanto la capa de infraestructura
 * (`data.preferences.SettingsPreferences`, basada en Android
 * SharedPreferences) como la capa de feature (`features.settings.*`) deben
 * poder depender de este contrato sin invertir la direccion de dependencias
 * del proyecto (UI -> ViewModel -> Manager -> Repository -> Data Source ->
 * Android APIs, ver PROJECT_CONTEXT.md).
 *
 * Deliberadamente generico (String/Boolean/Int) y sin claves de negocio
 * predefinidas: Settings Core Foundation es infraestructura, no una
 * funcionalidad final (ver Sprint 06, engineering/00_Project). Los tipos
 * Float/Long/Set<String> quedan fuera de alcance de esta primera version.
 */
interface SettingsStore {

    fun getString(key: String, default: String? = null): String?

    fun putString(key: String, value: String)

    fun getBoolean(key: String, default: Boolean = false): Boolean

    fun putBoolean(key: String, value: Boolean)

    fun getInt(key: String, default: Int = 0): Int

    fun putInt(key: String, value: Int)

    fun remove(key: String)

    fun contains(key: String): Boolean

    /**
     * Copia de todos los valores actualmente persistidos, sin distincion de
     * tipo (equivalente a `SharedPreferences.getAll()`). Se usa unicamente
     * para hidratar [com.uriel.logpose.features.settings.SettingsSession] al
     * arrancar; el codigo de negocio nunca deberia depender de esto.
     */
    fun snapshot(): Map<String, Any?>

    fun clear()
}
