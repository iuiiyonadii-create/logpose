package com.thamis.lab.simulation

import com.thamis.lab.core.contracts.event.LabEvent
import com.thamis.lab.core.contracts.snapshot.CognitiveSnapshot
import com.thamis.lab.simulation.environment.EnvironmentSimulator
import com.thamis.lab.simulation.fault.FaultInjectionEngine
import com.thamis.lab.simulation.fault.FaultType
import com.thamis.lab.simulation.scenario.ScenarioBuilder
import com.thamis.lab.simulation.scenario.ScenarioExecutor
import com.thamis.lab.simulation.scenario.ScenarioRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulationEngineTest {

    @Test
    fun testScenarioBuilderAndExecution() {
        val scenario = ScenarioBuilder("scen-100", "Music Playback Test")
            .description("Tests playing music while riding at 45km/h")
            .initialSnapshot(CognitiveSnapshot(timestampMs = 0L))
            .addEvent(LabEvent.TextCommandEvent("evt-1", timestampMs = 500L, userText = "poné música"))
            .expectedIntent("PLAY_MUSIC")
            .build()

        assertEquals("scen-100", scenario.scenarioId)
        assertEquals(1, scenario.events.size)

        val executor = ScenarioExecutor()
        val result = executor.executeScenario(scenario)

        assertTrue(result.isPassed)
        assertEquals("PLAY_MUSIC", result.actualIntentMatched)
    }

    @Test
    fun testEnvironmentSimulatorStateUpdates() {
        val env = EnvironmentSimulator()
        env.simulateBluetoothConnection("Cardo Freecom 4+", isIntercom = true)
        env.simulateGpsSpeed(60.0)

        val snapshot = env.currentSnapshot
        assertTrue(snapshot.bluetoothState.isConnected)
        assertEquals("Cardo Freecom 4+", snapshot.bluetoothState.deviceName)
        assertEquals(60.0, snapshot.locationState.speedKmH, 0.01)
    }

    @Test
    fun testFaultInjectionEngine() {
        val faultEngine = FaultInjectionEngine()
        val fault = faultEngine.injectDeterministicFault(
            eventId = "f-1",
            timestampMs = 1000L,
            faultType = FaultType.BLUETOOTH_DROP,
            targetComponent = "BluetoothManager"
        )

        assertEquals("BLUETOOTH_DROP", fault.faultType)
        assertEquals(1, faultEngine.activeFaults.size)
    }

    @Test
    fun testScenarioRepositoryAndHighVolume() {
        val repo = ScenarioRepository()
        val count = 1000

        val startTime = System.currentTimeMillis()
        for (i in 1..count) {
            val scen = ScenarioBuilder("scen-$i", "Scenario $i")
                .initialSnapshot(CognitiveSnapshot(timestampMs = i.toLong()))
                .build()
            repo.save(scen)
        }
        val duration = System.currentTimeMillis() - startTime

        assertEquals(count, repo.getAll().size)
        assertTrue("Saving 1,000 scenarios must take less than 500ms", duration < 500)
    }
}
