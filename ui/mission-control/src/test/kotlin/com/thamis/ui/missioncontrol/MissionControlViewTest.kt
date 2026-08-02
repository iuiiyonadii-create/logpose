package com.thamis.ui.missioncontrol

import com.thamis.lab.performance.device.ConnectionType
import com.thamis.lab.performance.device.DeviceInfo
import com.thamis.lab.performance.device.DeviceState
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MissionControlViewTest {

    @Test
    fun testMissionControlHtmlGenerationWithDevices() {
        val usbDev = DeviceInfo("TKDMZPZDZ5MR8XNV", "2409BRN2CA", isEmulator = false, state = DeviceState.ONLINE, connectionType = ConnectionType.USB, rawModel = "2409BRN2CA")
        val wifiDev = DeviceInfo("adb-TKDMZPZDZ5MR8XNV-T8B1CJ._adb-tls-connect._tcp", "2409BRN2CA", isEmulator = false, state = DeviceState.ONLINE, connectionType = ConnectionType.WIFI, rawModel = "2409BRN2CA")

        val html = MissionControlView.generateDashboardHtml(listOf(usbDev, wifiDev), selectedSerial = usbDev.deviceId)

        assertNotNull(html)
        assertTrue(html.contains("2409BRN2CA (USB)"))
        assertTrue(html.contains("2409BRN2CA (WiFi)"))
        assertTrue(html.contains("class=\"device-selector\""))
        assertTrue(html.contains("INICIAR LABORATORIO"))
    }
}
