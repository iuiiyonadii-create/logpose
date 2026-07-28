package com.uriel.logpose.thamis_ai.ecosystem

import android.util.Log

/**
 * Lifecycle manager for THAMIS plugins.
 */
class PluginManager {

    private val plugins = mutableMapOf<String, Plugin>()

    fun register(plugin: Plugin) {
        Log.d("PluginManager", "Registering plugin: ${plugin.name}")
        plugins[plugin.id] = plugin
        plugin.onInitialize()
    }

    fun broadcast(event: String, data: Any?) {
        plugins.values.forEach { it.onEvent(event, data) }
    }
}
