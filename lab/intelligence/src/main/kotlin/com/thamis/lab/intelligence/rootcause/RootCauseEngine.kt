package com.thamis.lab.intelligence.rootcause

import com.thamis.lab.core.contracts.event.LabEvent

public data class RootCauseReport(
    public val rootEventId: String?,
    public val rootCauseReason: String,
    public val failureChain: List<String>,
    public val impactLevel: String
)

/**
 * Root Cause Engine for stack trace, event correlation, and failure chain analysis.
 */
public class RootCauseEngine {

    public fun analyzeRootCause(
        events: List<LabEvent>,
        failureMessage: String?
    ): RootCauseReport {
        val faultEvent = events.filterIsInstance<LabEvent.FaultInjectedEvent>().firstOrNull()
        val rootId = faultEvent?.eventId ?: events.firstOrNull()?.eventId
        val reason = faultEvent?.let { "Injected fault '${it.faultType}' on component '${it.targetComponent}'" }
            ?: (failureMessage ?: "Unknown failure")

        val chain = events.map { "${it.timestampMs}ms: ${it.eventId}" }
        val impact = if (faultEvent != null) "HIGH" else "MEDIUM"

        return RootCauseReport(
            rootEventId = rootId,
            rootCauseReason = reason,
            failureChain = chain,
            impactLevel = impact
        )
    }
}
