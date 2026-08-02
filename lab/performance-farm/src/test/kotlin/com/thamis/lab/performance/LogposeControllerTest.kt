package com.thamis.lab.performance

import com.thamis.lab.performance.logpose.RealLogposeController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LogposeControllerTest {

    @Test
    fun testRealLogposeControllerMethods() {
        val controller = RealLogposeController()
        val devId = "emulator-5554"

        assertEquals("com.uriel.logpose", controller.packageName)

        val status = controller.queryLiveStatus(devId)
        assertNotNull(status)
        assertEquals("com.uriel.logpose", status.packageName)

        val launchRes = controller.launchLogposeApp(devId)
        assertTrue(launchRes.isSuccess)

        val stopRes = controller.forceStopLogposeApp(devId)
        assertTrue(stopRes.isSuccess)
    }
}
