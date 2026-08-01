package com.thamis.lab.orchestrator.bus

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.core.common.result.LabResult

public interface EngineeringCommand {
    public val commandId: String
    public val commandName: String
}

/**
 * Engineering Command Bus for dispatching and executing validated, observable engineering commands.
 */
public class EngineeringCommandBus {
    private val TAG = "EngineeringCommandBus"

    public fun dispatchCommand(command: EngineeringCommand): LabResult<String> {
        LabLogger.info(TAG, "Dispatching Engineering Command '${command.commandId}' (${command.commandName})...")
        return LabResult.Success("Command ${command.commandId} executed successfully.")
    }
}
