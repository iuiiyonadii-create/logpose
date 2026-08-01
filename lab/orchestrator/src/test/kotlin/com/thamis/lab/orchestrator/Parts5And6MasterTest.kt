package com.thamis.lab.orchestrator

import com.thamis.lab.intelligence.health.ProjectHealthEngine
import com.thamis.lab.intelligence.twin.RepositoryDigitalTwinEngine
import com.thamis.lab.orchestrator.execution.ExecutionQueueManager
import com.thamis.lab.orchestrator.execution.QueuedExecutionTask
import com.thamis.lab.performance.adb.AdbOrchestratorEngine
import com.thamis.lab.performance.device.RealDeviceInventoryEngine
import com.thamis.lab.simulation.failure.FailureReproductionEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Parts5And6MasterTest {

    @Test
    fun testExecutionQueueAndRepositoryDigitalTwin() {
        val queueManager = ExecutionQueueManager()
        val twinEngine = RepositoryDigitalTwinEngine()

        val task = QueuedExecutionTask("task-1", "CAMPAIGN", 1, "Payload", System.currentTimeMillis())
        queueManager.enqueueTask(task)
        assertEquals(1, queueManager.getQueueSize())

        val polled = queueManager.pollNextTask()
        assertNotNull(polled)
        assertEquals("task-1", polled?.taskId)

        val twinState = twinEngine.syncRepositoryTwin()
        assertEquals(10, twinState.totalModules)
        assertEquals(100.0, twinState.architectureHealthScore, 0.01)
    }

    @Test
    fun testDeviceInventoryAdbOrchestratorFailureReproductionAndHealth() {
        val inventoryEngine = RealDeviceInventoryEngine()
        val adbOrchestrator = AdbOrchestratorEngine()
        val failureEngine = FailureReproductionEngine()
        val healthEngine = ProjectHealthEngine()
        val devId = "TKDMZPZDZ5MR8XNV"

        val profile = inventoryEngine.inspectAndRegisterDevice(devId)
        assertNotNull(profile)
        assertEquals(devId, profile.targetSerial)

        val connectRes = adbOrchestrator.orchestrateWirelessConnect("192.168.1.50:5555")
        assertTrue(connectRes.isSuccess)

        val artifact = failureEngine.captureAndBuildReproducibleScenario("fail-101", "scen-2", "NullPointerException")
        assertNotNull(artifact)
        assertTrue(artifact.reproducibleScenario.scenarioId.contains("fail-101"))

        val health = healthEngine.calculateProjectHealth()
        assertEquals(100.0, health.healthScore, 0.01)
    }
}
