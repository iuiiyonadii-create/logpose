package com.uriel.logpose.logprobe.windows

import com.uriel.logpose.logprobe.models.WindowSnapshot

object ActivityDump {

    fun distinctActivities(): List<String> =
        WindowProbe.currentWindowEvents()
            .mapNotNull { it.className }
            .distinct()

    fun activitiesByPackage(): Map<String, List<String>> =
        WindowProbe.currentWindowEvents()
            .groupBy(WindowSnapshot::packageName) { it.className.orEmpty() }
}
