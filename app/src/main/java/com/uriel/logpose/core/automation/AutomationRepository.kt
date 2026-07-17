package com.uriel.logpose.core.automation

object AutomationRepository {

    private val rules =
        mutableListOf<AutomationRule>()

    fun add(
        rule: AutomationRule
    ) {
        rules.add(rule)
    }

    fun remove(
        rule: AutomationRule
    ) {
        rules.remove(rule)
    }

    fun all(): List<AutomationRule> =
        rules.toList()

    fun enabled(): List<AutomationRule> =
        rules.filter {
            it.enabled
        }

    fun clear() {
        rules.clear()
    }
}