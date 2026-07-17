package com.uriel.logpose.features.settings

/**
 * Estado observable por la UI (Compose) via [SettingsManager]. Snapshot
 * inmutable de todos los valores actualmente en [SettingsSession], separados
 * por tipo para evitar castings en la capa de presentacion.
 */
data class SettingsState(
    val strings: Map<String, String> = emptyMap(),
    val booleans: Map<String, Boolean> = emptyMap(),
    val ints: Map<String, Int> = emptyMap(),
    val isLoaded: Boolean = false
)
