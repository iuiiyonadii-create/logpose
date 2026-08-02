package com.thamis.lab.performance

import com.thamis.lab.performance.adb.AdvancedAdbEngine
import com.thamis.lab.performance.device.DeviceInfo
import com.thamis.lab.performance.device.DeviceState
import com.thamis.lab.performance.emulator.EmulatorFactory
import com.thamis.lab.performance.farm.MultiDeviceFarmPool
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FarmAndEnvironmentTest {

    @Test
    fun testMultiDeviceFarmPoolAndAdvancedAdb() {
        val pool = MultiDeviceFarmPool()
        val adb = AdvancedAdbEngine()
        val devId = "TKDMZPZDZ5MR8XNV"

        pool.registerDevice(DeviceInfo(devId, "Redmi 2409BRN2CA", isEmulator = false, state = DeviceState.ONLINE))
        assertEquals(1, pool.getPoolSize())

        val res = pool.reserveDevice(devId)
        assertNotNull(res)
        assertEquals(devId, res?.assignedDeviceSerial)

        val grantRes = adb.grantPermission(devId, "com.uriel.logpose", "android.permission.RECORD_AUDIO")
        assertTrue(grantRes.isSuccess)
    }

    @Test
    fun testEmulatorFactory() {
        val factory = EmulatorFactory()
        val template = factory.createTemplate("Samsung", "Galaxy S24", 34)
        val emu = factory.provisionEmulator(template, "5556")

        assertNotNull(emu)
        assertEquals("emulator-5556", emu.deviceId)
    }
}
