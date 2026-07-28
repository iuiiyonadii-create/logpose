package com.uriel.logpose.thamis.validation

import com.uriel.logpose.thamis.navigation.DestinationResolver
import com.uriel.logpose.thamis.navigation.NavigationSafetyGate
import com.uriel.logpose.thamis.navigation.model.*
import org.junit.Test
import org.junit.Assert.*

/**
 * THAMIS_NAVIGATION_HARDENING_STRESS_TEST v1.0
 */
class NavigationSafetyStressTest {

    @Test
    fun testNormalNavigation() {
        println("\nTEST 1: Navegación normal - Llevame a casa")
        val context = NavigationContext(gpsAvailable = true, currentLocation = null, activeRoute = null, destination = null, speedKmh = 0)
        val goalType = DestinationResolver.resolve("llevame a casa")
        val goal = NavigationGoal(goalType, "Casa", 1f, 0.92f)
        val decision = NavigationDecision(goal, "Casa", 0.92f, 0.5f, false, "Normal Test")
        
        val result = NavigationSafetyGate.validate(decision, context)
        assertEquals(NavigationSafetyGate.Result.APPROVED, result)
    }

    @Test
    fun testHighSpeedConfirmation() {
        println("\nTEST 2: Alta velocidad (110 km/h)")
        val context = NavigationContext(gpsAvailable = true, currentLocation = null, activeRoute = null, destination = null, speedKmh = 110)
        val goal = NavigationGoal(NavigationGoal.GoalType.GO_HOME, "Casa", 1f, 0.95f)
        val decision = NavigationDecision(goal, "Casa", 0.95f, 0.5f, false, "Speed Test")
        
        val result = NavigationSafetyGate.validate(decision, context)
        assertEquals(NavigationSafetyGate.Result.CONFIRM_REQUIRED, result)
    }

    @Test
    fun testGpsUnavailableReject() {
        println("\nTEST 5: Llevame al trabajo sin GPS")
        val context = NavigationContext(gpsAvailable = false, currentLocation = null, activeRoute = null, destination = null, speedKmh = 40)
        val goal = NavigationGoal(NavigationGoal.GoalType.GO_WORK, "Trabajo", 1f, 0.9f)
        val decision = NavigationDecision(goal, "Trabajo", 0.9f, 0.5f, false, "No GPS Test")
        
        val result = NavigationSafetyGate.validate(decision, context)
        assertEquals(NavigationSafetyGate.Result.REJECTED, result)
    }
}
