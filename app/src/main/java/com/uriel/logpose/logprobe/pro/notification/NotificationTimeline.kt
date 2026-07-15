package com.uriel.logpose.logprobe.notification

import com.uriel.logpose.logprobe.models.NotificationSnapshot

object NotificationTimeline {

    fun ordered(): List<NotificationSnapshot> =
        NotificationHistory.all().sortedBy { it.postedAtMillis }
}
