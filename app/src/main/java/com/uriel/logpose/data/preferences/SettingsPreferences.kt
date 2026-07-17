package com.uriel.logpose.data.preferences

import android.content.Context
import com.uriel.logpose.domain.settings.SettingsStore

/**
 * Data Source de Settings Core. Sigue exactamente el mismo patron que
 * [DevicePreferences] (SharedPreferences simple, sin dependencias nuevas):
 * el proyecto no declara DataStore ni ninguna libreria de persistencia
 * (ver app/build.gradle.kts), asi que Settings Core no la introduce.
 *
 * Usa un archivo de preferencias propio ("logpose_settings") distinto del
 * de [DevicePreferences] ("logpose_preferences") para no mezclar la
 * seleccion de dispositivo Bluetooth (una decision de Bluetooth Core) con
 * ajustes generales de la app (responsabilidad de Settings Core).
 */
class SettingsPreferences(context: Context) : SettingsStore {

    private val preferences =
        context.applicationContext.getSharedPreferences(
            "logpose_settings",
            Context.MODE_PRIVATE
        )

    override fun getString(key: String, default: String?): String? =
        preferences.getString(key, default)

    override fun putString(key: String, value: String) {
        preferences.edit()
            .putString(key, value)
            .apply()
    }

    override fun getBoolean(key: String, default: Boolean): Boolean =
        preferences.getBoolean(key, default)

    override fun putBoolean(key: String, value: Boolean) {
        preferences.edit()
            .putBoolean(key, value)
            .apply()
    }

    override fun getInt(key: String, default: Int): Int =
        preferences.getInt(key, default)

    override fun putInt(key: String, value: Int) {
        preferences.edit()
            .putInt(key, value)
            .apply()
    }

    override fun remove(key: String) {
        preferences.edit()
            .remove(key)
            .apply()
    }

    override fun contains(key: String): Boolean =
        preferences.contains(key)

    override fun snapshot(): Map<String, Any?> =
        preferences.all

    override fun clear() {
        preferences.edit()
            .clear()
            .apply()
    }
}
