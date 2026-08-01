package com.thamis.lab.core.contracts.response

import com.thamis.lab.core.contracts.decision.CognitiveDecision
import com.thamis.lab.core.contracts.version.MessageVersion

/**
 * Polymorphic response contract for lab command execution results.
 */
public sealed interface LabResponse {
    public val responseId: String
    public val commandId: String
    public val timestampMs: Long
    public val version: MessageVersion

    public data class ScenarioExecutionResponse(
        override val responseId: String,
        override val commandId: String,
        override val timestampMs: Long,
        override val version: MessageVersion = MessageVersion(),
        public val isPassed: Boolean,
        public val decision: CognitiveDecision?,
        public val executionDurationMs: Long
    ) : LabResponse

    public data class TelemetryResponse(
        override val responseId: String,
        override val commandId: String,
        override val timestampMs: Long,
        override val version: MessageVersion = MessageVersion(),
        public val metricName: String,
        public val metricValue: Double
    ) : LabResponse

    public data class ErrorResponse(
        override val responseId: String,
        override val commandId: String,
        override val timestampMs: Long,
        override val version: MessageVersion = MessageVersion(),
        public val errorMessage: String,
        public val errorCode: Int = -1
    ) : LabResponse
}
