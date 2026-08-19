package com.uriel.logpose.thamis

import com.uriel.logpose.thamis.intent.IntentDetector
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.world.engine.WorldModelEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ComplexArgentinoTest {

    @Test
    fun testArgentineMusicCommand() {
        val input = "che ponete un temon de duki"
        val result = IntentDetector.detect(input, ignoreWakeWord = true)
        assertEquals(Intent.PLAY_MUSIC, result.intent)
    }

    @Test
    fun testSafetyAlertSlang() {
        val input = "ojo que hay cana"
        val result = IntentDetector.detect(input, ignoreWakeWord = true)
        assertEquals(Intent.SAFETY_ALERT, result.intent)
    }

    @Test
    fun testNavigationSlang() {
        val input = "llevame al laburo"
        val result = IntentDetector.detect(input, ignoreWakeWord = true)
        assertEquals(Intent.NAVIGATE, result.intent)
    }

    @Test
    fun testVehicleDiagnosticsSlang() {
        val input = "como esta la moto"
        val result = IntentDetector.detect(input, ignoreWakeWord = true)
        assertEquals(Intent.VEHICLE_STATUS, result.intent)
    }

    @Test
    fun testProactiveFuelWarning() {
        // Simular actualización de mundo con poca nafta
        WorldModelEngine.update("Test") { 
            it.copy(vehicle = it.vehicle.copy(fuelLevelPct = 5)) 
        }
        val snapshot = WorldModelEngine.getSnapshot()
        assertEquals(5, snapshot.vehicle.fuelLevelPct)
    }
}
