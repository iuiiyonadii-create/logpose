package com.uriel.logpose.logprobe.media

import com.uriel.logpose.logprobe.models.MediaSnapshot
import com.uriel.logpose.logprobe.models.ProbeEventType
import com.uriel.logpose.logprobe.reports.ProbeTimeline

object MediaHistory {
    fun all(): List<MediaSnapshot> =
        ProbeTimeline.snapshot()
            .filter { it.type == ProbeEventType.MEDIA_STATE }
            .mapNotNull { it.snapshot as? MediaSnapshot }
}
