package com.uriel.logpose.thamis.validation

import com.uriel.logpose.thamis.navigation.DestinationResolver
import com.uriel.logpose.thamis.navigation.NavigationSafetyGate
import com.uriel.logpose.thamis.navigation.model.*
import org.junit.Test
import org.junit.Assert.*

/**
 * THAMIS_NAVIGATION_STRESS_TEST v3.0
 */
class NavigationStressTest {

    @Test
    fun testGoHome() {
        println("\nTEST 1: Llevame a casa")
        val goalType = DestinationResolver.resolve("llevame a casa")
        assertEquals(NavigationGoal.GoalType.GO_HOME, goalType)
    }

    @Test
    fun testGpsUnavailable() {
        println("\nTEST 3: Inicia ruta sin GPS")
        val context = NavigationContext(gpsAvailable = false, currentLocation = null, activeRoute = null, destination = null, speedKmh = 0)
        val goal = NavigationGoal(NavigationGoal.GoalType.GO_HOME, "Casa", 1f, 0.9f)
        val decision = NavigationDecision(goal, "Casa", 0.9f, 0.5f, false, "Test")
        
        val result = NavigationSafetyGate.validate(decision, context)
        assertEquals(NavigationSafetyGate.Result.REJECTED, result)
    }

    @Test
    fun testSpeedLimit() {
        println("\nTEST 5: Moto a 130 km/h")
        val context = NavigationContext(gpsAvailable = true, currentLocation = "Ruta 2", activeRoute = null, destination = null, speedKmh = 130)
        val goal = NavigationGoal(NavigationGoal.GoalType.GO_HOME, "Casa", 1f, 0.95f)
        val decision = NavigationDecision(goal, "Casa", 0.95f, 0.5f, false, "High Speed Test")
        
        val result = NavigationSafetyGate.validate(decision, context)
        assertEquals(NavigationSafetyGate.Result.BLOCKED, result)
    }
}
