package com.thamis.lab.orchestrator.sdk

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.core.common.result.LabResult
import java.util.concurrent.ConcurrentHashMap

public interface ThamisPlugin {
    public val pluginId: String
    public val pluginVersion: String
    public val capabilities: List<String>
    public fun initialize(): LabResult<String>
}

/**
 * Plugin SDK enabling third parties to extend THAMIS LAB with hot-loadable simulation, ADB, Bluetooth, and AI plugins.
 */
public class PluginSdk {
    private val TAG = "PluginSdk"
    private val plugins = ConcurrentHashMap<String, ThamisPlugin>()

    public fun registerPlugin(plugin: ThamisPlugin): LabResult<String> {
        plugins[plugin.pluginId] = plugin
        val res = plugin.initialize()
        LabLogger.info(TAG, "Plugin '${plugin.pluginId}' v${plugin.pluginVersion} registered: ${res.isSuccess}")
        return LabResult.Success("Plugin ${plugin.pluginId} registered successfully.")
    }

    public fun getRegisteredPlugins(): List<ThamisPlugin> = plugins.values.toList()
}
