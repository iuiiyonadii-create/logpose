package com.uriel.logpose.logprobe.models

data class ProbeReport(
    val sessionId: String,
    val startedAtMillis: Long,
    val endedAtMillis: Long,
    val allowedPackages: Set<String>,
    val events: List<ProbeEvent>
) {
    val eventCount: Int get() = events.size
}
