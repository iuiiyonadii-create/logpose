package com.thamis.lab.core.contracts.command

import com.thamis.lab.core.contracts.version.MessageVersion

/**
 * Polymorphic command contract for lab orchestrators and simulators.
 */
public sealed interface LabCommand {
    public val commandId: String
    public val timestampMs: Long
    public val version: MessageVersion

    public data class RunScenarioCommand(
        override val commandId: String,
        override val timestampMs: Long,
        override val version: MessageVersion = MessageVersion(),
        public val scenarioId: String,
        public val maxDurationMs: Long = 10000
    ) : LabCommand

    public data class InjectFaultCommand(
        override val commandId: String,
        override val timestampMs: Long,
        override val version: MessageVersion = MessageVersion(),
        public val faultType: String,
        public val targetComponent: String
    ) : LabCommand

    public data class CaptureTelemetryCommand(
        override val commandId: String,
        override val timestampMs: Long,
        override val version: MessageVersion = MessageVersion(),
        public val metricTarget: String
    ) : LabCommand
}
