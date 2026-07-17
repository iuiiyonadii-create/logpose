package com.uriel.logpose.core.automation

object AutomationStatistics {

    private var executed = 0

    fun register() {
        executed++
    }

    fun total(): Int =
        executed

    fun reset() {
        executed = 0
    }
}