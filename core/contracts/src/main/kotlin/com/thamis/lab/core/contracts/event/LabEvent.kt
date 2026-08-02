package com.thamis.lab.core.contracts.event

import com.thamis.lab.core.contracts.snapshot.CognitiveSnapshot
import com.thamis.lab.core.contracts.version.MessageVersion

/**
 * Polymorphic event contract for THAMIS Lab simulation system.
 */
public sealed interface LabEvent {
    public val eventId: String
    public val timestampMs: Long
    public val version: MessageVersion

    public data class PerceptionEvent(
        override val eventId: String,
        override val timestampMs: Long,
        override val version: MessageVersion = MessageVersion(),
        public val snapshot: CognitiveSnapshot
    ) : LabEvent

    public data class TextCommandEvent(
        override val eventId: String,
        override val timestampMs: Long,
        override val version: MessageVersion = MessageVersion(),
        public val userText: String,
        public val sttConfidence: Double = 1.0
    ) : LabEvent

    public data class SystemStateEvent(
        override val eventId: String,
        override val timestampMs: Long,
        override val version: MessageVersion = MessageVersion(),
        public val stateName: String,
        public val payload: String
    ) : LabEvent

    public data class FaultInjectedEvent(
        override val eventId: String,
        override val timestampMs: Long,
        override val version: MessageVersion = MessageVersion(),
        public val faultType: String,
        public val targetComponent: String
    ) : LabEvent
}
