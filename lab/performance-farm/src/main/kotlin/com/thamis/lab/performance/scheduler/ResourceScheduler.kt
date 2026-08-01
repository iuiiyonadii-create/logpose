package com.thamis.lab.performance.scheduler

import com.thamis.lab.performance.device.DeviceInfo
import com.thamis.lab.performance.device.DeviceRegistry
import java.util.concurrent.atomic.AtomicInteger

/**
 * Load-balancer that schedules simulation scenarios across available device farm nodes.
 */
public class ResourceScheduler(public val deviceRegistry: DeviceRegistry) {
    private val roundRobinCounter = AtomicInteger(0)

    public fun selectNextAvailableDevice(): DeviceInfo? {
        val available = deviceRegistry.getAvailableDevices()
        if (available.isEmpty()) return null
        val index = Math.abs(roundRobinCounter.getAndIncrement() % available.size)
        return available[index]
    }
}
