package com.thamis.lab.performance

import com.thamis.lab.performance.adb.AdbManager
import com.thamis.lab.performance.device.ConnectionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdbDiscoveryTest {

    @Test
    fun testRealUserAdbDevicesLOutputParsing() {
        val adb = AdbManager()
        val realAdbOutput = """
            List of devices attached
            TKDMZPZDZ5MR8XNV device product:pond_global model:2409BRN2CA device:pond transport_id:3
            adb-TKDMZPZDZ5MR8XNV-T8B1CJ._adb-tls-connect._tcp device product:pond_global model:2409BRN2CA device:pond transport_id:4
        """.trimIndent()

        val devices = adb.parseAdbDevicesOutput(realAdbOutput)

        // Must detect BOTH devices without discarding either
        assertEquals(2, devices.size)

        val usbDev = devices.first { it.connectionType == ConnectionType.USB }
        val wifiDev = devices.first { it.connectionType == ConnectionType.WIFI }

        assertEquals("TKDMZPZDZ5MR8XNV", usbDev.deviceId)
        assertEquals("2409BRN2CA", usbDev.rawModel)
        assertEquals("2409BRN2CA (USB)", usbDev.displayName)

        assertEquals("adb-TKDMZPZDZ5MR8XNV-T8B1CJ._adb-tls-connect._tcp", wifiDev.deviceId)
        assertEquals("2409BRN2CA", wifiDev.rawModel)
        assertEquals("2409BRN2CA (WiFi)", wifiDev.displayName)

        // Verify targeted ADB command uses adb -s <serial>
        val cmdResult = adb.executeTargetedAdbCommand(wifiDev.adbTargetSerial, "shell getprop ro.build.version.release")
        assertTrue(cmdResult.isSuccess)
        assertTrue(cmdResult.getOrNull()!!.contains("adb -s adb-TKDMZPZDZ5MR8XNV-T8B1CJ._adb-tls-connect._tcp"))
    }
}
