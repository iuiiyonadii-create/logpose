package com.uriel.logpose.logprobe.collectors

import com.uriel.logpose.logprobe.common.ProbeGuard
import com.uriel.logpose.logprobe.common.ProbeUtils
import com.uriel.logpose.logprobe.media.MediaProbe
import com.uriel.logpose.logprobe.models.ProbeEvent
import com.uriel.logpose.logprobe.models.ProbeEventType
import com.uriel.logpose.logprobe.reports.ProbeTimeline
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

class MediaCollector(
    private val probe: MediaProbe,
    private val pollIntervalMillis: Long = 5_000L
) : PollingCollector {

    @Volatile private var running = false

    override suspend fun start() {
        running = true
        while (coroutineContext.isActive && running) {
            val session = ProbeGuard.currentSession()
            if (session != null) {
                probe.currentSnapshots().forEach { snapshot ->
                    ProbeTimeline.record(
                        ProbeEvent(
                            id = ProbeUtils.newEventId(),
                            sessionId = session.sessionId,
                            type = ProbeEventType.MEDIA_STATE,
                            timestampMillis = snapshot.timestampMillis,
                            sourcePackage = snapshot.packageName,
                            snapshot = snapshot
                        )
                    )
                }
            }
            delay(pollIntervalMillis)
        }
    }

    override fun stop() {
        running = false
    }

    override fun isRunning(): Boolean = running
}
