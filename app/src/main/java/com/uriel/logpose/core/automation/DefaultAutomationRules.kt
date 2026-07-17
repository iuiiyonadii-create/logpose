package com.uriel.logpose.core.automation

object DefaultAutomationRules {

    fun load() {

        AutomationRepository.clear()

        AutomationRepository.add(

            AutomationRule(

                trigger =
                    AutomationTrigger.BLUETOOTH_CONNECTED,

                action =
                    AutomationAction.START_LISTENING
            )
        )

        AutomationRepository.add(

            AutomationRule(

                trigger =
                    AutomationTrigger.BLUETOOTH_DISCONNECTED,

                action =
                    AutomationAction.STOP_LISTENING
            )
        )
    }
}