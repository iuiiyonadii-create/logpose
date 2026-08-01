package com.thamis.lab.simulation

import com.thamis.lab.simulation.world.AdvancedGpsEngine
import com.thamis.lab.simulation.world.BluetoothWorldSimulator
import com.thamis.lab.simulation.world.NetworkLabSimulator
import com.thamis.lab.simulation.world.NotificationLabSimulator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WorldSimulatorTest {

    @Test
    fun testWorldSimulators() {
        val btWorld = BluetoothWorldSimulator()
        val gpsEngine = AdvancedGpsEngine()
        val netLab = NetworkLabSimulator()
        val notifLab = NotificationLabSimulator()

        val btEnv = btWorld.simulateEnvironment("Sena 50S")
        assertEquals("Sena 50S", btEnv.activeIntercomModel)

        val route = gpsEngine.simulateRoute("City-Center")
        assertEquals("City-Center", route.routeName)

        val net = netLab.simulateNetwork("5G")
        assertEquals("5G", net.networkType)

        val notifs = notifLab.generateNotificationStream()
        assertTrue(notifs.isNotEmpty())
    }
}
