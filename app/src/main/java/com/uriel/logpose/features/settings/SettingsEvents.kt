package com.uriel.logpose.features.settings

/**
 * Eventos puntuales que [SettingsRepository] emite hacia capas superiores
 * (ViewModel/futuras features), independientes del [SettingsState] continuo.
 * Utiles para efectos de un solo disparo, como invalidar una cache que
 * depende de un ajuste especifico.
 */
sealed class SettingsCoreEvent {

    data class Changed(val key: String) : SettingsCoreEvent()

    data class Removed(val key: String) : SettingsCoreEvent()

    data object Loaded : SettingsCoreEvent()

    data object Cleared : SettingsCoreEvent()
}
