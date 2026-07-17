package com.uriel.logpose.core.automation

data class AutomationRule(

    val trigger: AutomationTrigger,

    val action: AutomationAction,

    val enabled: Boolean = true
)