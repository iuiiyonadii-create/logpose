package com.uriel.logpose.thamis.navigation

import com.uriel.logpose.thamis.navigation.model.*
import org.junit.Test
import org.junit.Assert.*

/**
 * THAMIS_NAVIGATION_SAFETY_STRESS_TEST v1.0
 */
class NavigationSafetyStressTest {

    @Test
    fun testNormalNavigation() {
        println("\nTEST 1: Navegación normal - Llevame a casa")
        val context = NavigationContext(gpsAvailable = true, currentLocation = "Calle 1", activeRoute = null, destination = null, speedKmh = 0)
        val goalType = DestinationResolver.resolve("llevame a casa")
        val goal = NavigationGoal(goalType, "Casa", 1f, 0.91f)
        val decision = NavigationDecision(goal, "Casa", 0.91f, 0.5f, false, "Test")
        
        val result = NavigationSafetyGate.validate(decision, context)
        assertEquals(NavigationSafetyGate.Result.APPROVED, result)
        assertTrue(decision.confidence > 0.85f)
    }

    @Test
    fun testHighSpeed() {
        println("\nTEST 2: Alta velocidad - Llevame a casa")
        val context = NavigationContext(gpsAvailable = true, currentLocation = "Autopista", activeRoute = null, destination = null, speedKmh = 110)
        val goal = NavigationGoal(NavigationGoal.GoalType.GO_HOME, "Casa", 1f, 0.91f)
        val decision = NavigationDecision(goal, "Casa", 0.91f, 0.5f, true, "High Speed Test")
        
        val result = NavigationSafetyGate.validate(decision, context)
        assertEquals(NavigationSafetyGate.Result.CONFIRM_REQUIRED, result)
    }

    @Test
    fun testNoGps() {
        println("\nTEST 5: Sin GPS - Llevame al trabajo")
        val context = NavigationContext(gpsAvailable = false, currentLocation = null, activeRoute = null, destination = null, speedKmh = 0)
        val goal = NavigationGoal(NavigationGoal.GoalType.GO_WORK, "Trabajo", 1f, 0.88f)
        val decision = NavigationDecision(goal, "Trabajo", 0.88f, 0.5f, false, "No GPS Test")
        
        val result = NavigationSafetyGate.validate(decision, context)
        assertEquals(NavigationSafetyGate.Result.REJECTED, result)
    }

    @Test
    fun testPriorityGuard() {
        println("\nTEST 6: Prioridad - Llamada activa")
        val worldState = com.uriel.logpose.thamis.cognitive.model.WorldState(
            driving = com.uriel.logpose.thamis.cognitive.model.WorldState.DrivingState(),
            system = com.uriel.logpose.thamis.cognitive.model.WorldState.SystemState(activeCall = true),
            external = com.uriel.logpose.thamis.cognitive.model.WorldState.ExternalState()
        )
        
        assertTrue("Debe esperar por llamada activa", NavigationPriorityGuard.shouldWait(worldState))
    }
}
