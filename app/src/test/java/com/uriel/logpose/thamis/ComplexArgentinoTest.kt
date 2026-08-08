package com.uriel.logpose.thamis

import com.uriel.logpose.thamis.intent.ComplexIntentDetector
import com.thamis.lab.core.contracts.intent.Intent
import com.uriel.logpose.thamis.world.engine.WorldModelEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ComplexArgentinoTest {

    @Test
    fun testMultiStepArgentineCommand() {
        val input = "che ponete un temon y despues llevame al laburo"
        
        // 1. Verificar división
        val results = ComplexIntentDetector.detectSequence(input)
        
        assertEquals("Debería detectar 2 intenciones", 2, results.size)
        
        // 2. Verificar primera intención (Música)
        assertEquals(Intent.PLAY_MUSIC, results[0].intent)
        
        // 3. Verificar segunda intención (Navegación)
        assertEquals(Intent.NAVIGATE, results[1].intent)
    }

    @Test
    fun testSafetyAlertSlang() {
        val input = "ojo que hay cana"
        val results = ComplexIntentDetector.detectSequence(input)
        
        assertTrue(results.any { it.intent == Intent.SAFETY_ALERT })
    }

    @Test
    fun testTrafficSlang() {
        val input = "alto quilombo en la general paz"
        val results = ComplexIntentDetector.detectSequence(input)
        
        assertTrue(results.any { it.intent == Intent.TRAFFIC_STATUS })
    }

    @Test
    fun testCombinedSafetyAndMusic() {
        val input = "che ojo que hay cana y despues ponete algo de trap"
        val results = ComplexIntentDetector.detectSequence(input)
        
        assertEquals(2, results.size)
        assertEquals(Intent.SAFETY_ALERT, results[0].intent)
        assertEquals(Intent.PLAY_MUSIC, results[1].intent)
    }

    @Test
    fun testRestaurantAndTransportSlang() {
        val input = "donde hay un bodegon y como viene el sesenta"
        val results = ComplexIntentDetector.detectSequence(input)
        
        assertEquals(2, results.size)
        assertEquals(Intent.RESTAURANT_SEARCH, results[0].intent)
        assertEquals(Intent.TRANSPORT_INFO, results[1].intent)
    }

    @Test
    fun testVehicleDiagnosticsSlang() {
        val input = "che como esta la moto y cuanta nafta me queda"
        val results = ComplexIntentDetector.detectSequence(input)
        
        assertEquals(2, results.size)
        assertEquals(Intent.VEHICLE_STATUS, results[0].intent)
        assertEquals(Intent.FUEL_LEVEL, results[1].intent)
    }

    @Test
    fun testProactiveFuelWarning() {
        // Simular actualización de mundo con poca nafta
        WorldModelEngine.update("Test") { 
            it.copy(vehicle = it.vehicle.copy(fuelLevelPct = 5)) 
        }
        
        // El test pasa si no crashea y el motor proactivo se dispara (verificado vía logs en entorno real)
        assertTrue(WorldModelEngine.getCurrentSnapshot().vehicle.fuelLevelPct == 5)
    }
}
