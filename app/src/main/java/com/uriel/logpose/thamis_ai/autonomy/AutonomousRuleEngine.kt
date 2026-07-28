package com.uriel.logpose.thamis_ai.autonomy

/**
 * Defines hard rules for autonomous behavior.
 */
class AutonomousRuleEngine {
    fun shouldAutoStart(context: Map<String, Any>): Boolean {
        return context["is_riding"] == true && context["bluetooth_connected"] == true
    }
}
