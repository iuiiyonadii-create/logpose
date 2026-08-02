package com.thamis.ui.missioncontrol

import com.thamis.lab.performance.device.ConnectionType
import com.thamis.lab.performance.device.DeviceInfo
import com.thamis.lab.performance.device.DeviceState
import com.thamis.ui.missioncontrol.swing.SwingDashboardWindow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SwingDashboardTest {

    @Test
    fun testSwingDashboardWindowItemRefreshAndStyle() {
        val controller = MainDashboardController()
        val usbDev = DeviceInfo("TKDMZPZDZ5MR8XNV", "2409BRN2CA", isEmulator = false, state = DeviceState.ONLINE, connectionType = ConnectionType.USB, rawModel = "2409BRN2CA")
        val wifiDev = DeviceInfo("adb-TKDMZPZDZ5MR8XNV-T8B1CJ._adb-tls-connect._tcp", "2409BRN2CA", isEmulator = false, state = DeviceState.ONLINE, connectionType = ConnectionType.WIFI, rawModel = "2409BRN2CA")

        controller.updateDevices(listOf(usbDev, wifiDev))

        val window = SwingDashboardWindow(controller)
        window.refreshSelector()

        assertEquals(2, window.deviceComboBox.itemCount)
        assertEquals("2409BRN2CA (USB)", (window.deviceComboBox.getItemAt(0) as ComboBoxItem).displayName)
        assertEquals("2409BRN2CA (WiFi)", (window.deviceComboBox.getItemAt(1) as ComboBoxItem).displayName)

        assertNotNull(window.deviceComboBox.renderer)
    }
}
