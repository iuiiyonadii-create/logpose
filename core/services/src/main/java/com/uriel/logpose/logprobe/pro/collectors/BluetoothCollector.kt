package com.uriel.logpose.logprobe.collectors

import com.uriel.logpose.logprobe.bluetooth.BluetoothProbe
import com.uriel.logpose.logprobe.common.ProbeGuard
import com.uriel.logpose.logprobe.common.ProbeUtils
import com.uriel.logpose.logprobe.models.ProbeEvent
import com.uriel.logpose.logprobe.models.ProbeEventType
import com.uriel.logpose.logprobe.reports.ProbeTimeline
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

class BluetoothCollector(
    private val probe: BluetoothProbe,
    private val pollIntervalMillis: Long = 5_000L
) : PollingCollector {

    @Volatile private var running = false

    override suspend fun start() {
        running = true
        while (coroutineContext.isActive && running) {
            val session = ProbeGuard.currentSession()
            if (session != null) {
                val snapshot = probe.currentSnapshot()
                ProbeTimeline.record(
                    ProbeEvent(
                        id = ProbeUtils.newEventId(),
                        sessionId = session.sessionId,
                        type = ProbeEventType.BLUETOOTH_STATE,
                        timestampMillis = snapshot.timestampMillis,
                        sourcePackage = "android.bluetooth",
                        snapshot = snapshot
                    )
                )
            }
            delay(pollIntervalMillis)
        }
    }

    override fun stop() {
        running = false
    }

    override fun isRunning(): Boolean = running
}
