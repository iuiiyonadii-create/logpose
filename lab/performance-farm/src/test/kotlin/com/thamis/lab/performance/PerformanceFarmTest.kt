package com.thamis.lab.performance

import com.thamis.lab.performance.adb.AdbManager
import com.thamis.lab.performance.analyzer.PerformanceAnalyzer
import com.thamis.lab.performance.apk.ApkManager
import com.thamis.lab.performance.device.DeviceInfo
import com.thamis.lab.performance.device.DeviceRegistry
import com.thamis.lab.performance.device.DeviceState
import com.thamis.lab.performance.emulator.EmulatorManager
import com.thamis.lab.performance.scheduler.ResourceScheduler
import com.thamis.lab.performance.telemetry.HardwareTelemetry
import com.thamis.lab.performance.telemetry.HardwareTelemetryCollector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PerformanceFarmTest {

    @Test
    fun testDeviceRegistryAndDiscovery() {
        val registry = DeviceRegistry()
        val adb = AdbManager()

        val sampleAdbOutput = """
            List of devices attached
            emulator-5554	device
            emulator-5556	offline
            192.168.1.100:5555	device
        """.trimIndent()

        val discovered = adb.parseAdbDevicesOutput(sampleAdbOutput)
        assertEquals(3, discovered.size)

        for (dev in discovered) {
            registry.registerDevice(dev)
        }

        val available = registry.getAvailableDevices()
        assertEquals(2, available.size)
    }

    @Test
    fun testResourceSchedulerLoadBalancing() {
        val registry = DeviceRegistry()
        val d1 = DeviceInfo("dev-1", "Pixel 7", false, DeviceState.ONLINE)
        val d2 = DeviceInfo("dev-2", "Galaxy S23", false, DeviceState.ONLINE)

        registry.registerDevice(d1)
        registry.registerDevice(d2)

        val scheduler = ResourceScheduler(registry)
        val selected1 = scheduler.selectNextAvailableDevice()
        val selected2 = scheduler.selectNextAvailableDevice()

        assertNotNull(selected1)
        assertNotNull(selected2)
        assertTrue(selected1?.deviceId != selected2?.deviceId)
    }

    @Test
    fun testTelemetryCollectionAndPerformanceAnalyzer() {
        val collector = HardwareTelemetryCollector()
        val analyzer = PerformanceAnalyzer(collector, maxCpuThresholdPercent = 70.0, maxRamThresholdMb = 256.0)

        val devId = "emulator-5554"
        collector.recordTelemetry(HardwareTelemetry(timestampMs = 1000L, deviceId = devId, cpuPercent = 40.0, ramUsedMb = 120.0))
        collector.recordTelemetry(HardwareTelemetry(timestampMs = 2000L, deviceId = devId, cpuPercent = 60.0, ramUsedMb = 200.0))

        val report = analyzer.analyzeDevice(devId)
        assertEquals(50.0, report.avgCpuPercent, 0.01)
        assertEquals(200.0, report.peakRamMb, 0.01)
        assertTrue(report.isCpuPass)
        assertTrue(report.isRamPass)
    }

    @Test
    fun testApkAndEmulatorManager() {
        val emuManager = EmulatorManager()
        val apkManager = ApkManager()

        val emuResult = emuManager.launchEmulator("Pixel_7_API_34")
        assertTrue(emuResult.isSuccess)
        val emu = emuResult.getOrNull()!!

        val apkResult = apkManager.installApk(emu.deviceId, "/tmp/app.apk")
        assertTrue(apkResult.isSuccess)
    }
}
