package com.uriel.logpose.core.automation

object AutomationEngine {

    fun execute(

        trigger: AutomationTrigger

    ): List<AutomationResult> {

        val results =
            mutableListOf<AutomationResult>()

        AutomationRepository
            .enabled()
            .filter {
                it.trigger == trigger
            }
            .forEach {

                AutomationStatistics.register()

                results.add(

                    AutomationResult(

                        executed = true,

                        action = it.action
                    )
                )
            }

        return results
    }
}