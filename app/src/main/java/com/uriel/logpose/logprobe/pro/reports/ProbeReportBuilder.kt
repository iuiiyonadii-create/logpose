package com.uriel.logpose.logprobe.reports

import com.uriel.logpose.logprobe.common.ProbeClock
import com.uriel.logpose.logprobe.common.ProbeSession
import com.uriel.logpose.logprobe.models.ProbeReport

object ProbeReportBuilder {

    fun build(session: ProbeSession, endedAtMillis: Long = ProbeClock.nowMillis()): ProbeReport =
        ProbeReport(
            sessionId = session.sessionId,
            startedAtMillis = session.startedAtMillis,
            endedAtMillis = endedAtMillis,
            allowedPackages = session.allowedPackages,
            events = ProbeTimeline.snapshot()
        )
}
