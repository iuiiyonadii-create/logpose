package com.thamis.lab.orchestrator

import com.thamis.lab.intelligence.brain.ProjectBrainEngine
import com.thamis.lab.orchestrator.os.ThamisOperatingSystemCore
import com.thamis.lab.orchestrator.registry.RegisteredServiceDescriptor
import com.thamis.lab.orchestrator.registry.ServiceRegistryEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Prompts161To220MasterTest {

    @Test
    fun testOsCoreProjectBrainAndServiceRegistry() {
        val osCore = ThamisOperatingSystemCore()
        val brain = ProjectBrainEngine()
        val registry = ServiceRegistryEngine()

        val osStatus = osCore.inspectOperatingSystemStatus()
        assertTrue(osStatus.isOsOperational)
        assertEquals(100.0, osStatus.osHealthScore, 0.01)

        val brainState = brain.queryProjectBrain()
        assertNotNull(brainState)
        assertEquals(100.0, brainState.globalAwarenessScore, 0.01)

        registry.registerService(RegisteredServiceDescriptor("adbService", "AdbOrchestratorEngine", "1.0.0", true))
        val services = registry.getRegisteredServices()
        assertEquals(1, services.size)
        assertEquals("adbService", services.first().serviceName)
    }
}
