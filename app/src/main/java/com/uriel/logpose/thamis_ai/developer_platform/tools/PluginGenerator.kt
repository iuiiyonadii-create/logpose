package com.uriel.logpose.thamis_ai.developer_platform.tools

/**
 * Automates the generation of plugin boilerplate.
 */
class PluginGenerator {
    fun generateTemplate(name: String): String {
        return "class ${name}Plugin : Plugin { ... }"
    }
}
