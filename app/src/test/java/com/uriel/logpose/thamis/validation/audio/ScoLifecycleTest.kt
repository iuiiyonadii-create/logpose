package com.uriel.logpose.thamis.validation.audio

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * THAMIS_AUDIO_STRESS_TEST v3.0
 * Valida el ciclo de vida del ruteo de audio.
 */
class ScoLifecycleTest {

    @Before
    fun setup() {
        ScoLifecycleValidator.notifyStateChange(AudioRoutingTrace.ScoState.SCO_DISCONNECTED)
    }

    @Test
    fun testSuccessfulCycle() {
        println("\nTHAMIS_AUDIO_TEST: TEST 1 - Ciclo Exitoso")
        
        ScoLifecycleValidator.notifyStateChange(AudioRoutingTrace.ScoState.SCO_OPENING)
        ScoLifecycleValidator.notifyStateChange(AudioRoutingTrace.ScoState.LISTENING)
        ScoLifecycleValidator.notifyStateChange(AudioRoutingTrace.ScoState.REASONING)
        ScoLifecycleValidator.notifyStateChange(AudioRoutingTrace.ScoState.EXECUTING)
        ScoLifecycleValidator.notifyStateChange(AudioRoutingTrace.ScoState.SCO_RELEASED)
        
        assertEquals(AudioRoutingTrace.ScoState.SCO_RELEASED, ScoLifecycleValidator.getCurrentState())
    }

    @Test
    fun testCancellationTimeout() {
        println("\nTHAMIS_AUDIO_TEST: TEST 2 - Cancelación/Timeout")
        
        ScoLifecycleValidator.notifyStateChange(AudioRoutingTrace.ScoState.SCO_OPENING)
        ScoLifecycleValidator.notifyStateChange(AudioRoutingTrace.ScoState.LISTENING)
        // El usuario se arrepiente, cerramos sin decidir
        ScoLifecycleValidator.notifyStateChange(AudioRoutingTrace.ScoState.SCO_RELEASED)
        
        assertEquals(AudioRoutingTrace.ScoState.SCO_RELEASED, ScoLifecycleValidator.getCurrentState())
    }

    @Test
    fun testSequentialCommands() {
        println("\nTHAMIS_AUDIO_TEST: TEST 3 - Comandos Secuenciales")
        
        // Ciclo 1
        ScoLifecycleValidator.notifyStateChange(AudioRoutingTrace.ScoState.SCO_OPENING)
        ScoLifecycleValidator.notifyStateChange(AudioRoutingTrace.ScoState.SCO_RELEASED)
        
        // Ciclo 2
        ScoLifecycleValidator.notifyStateChange(AudioRoutingTrace.ScoState.SCO_OPENING)
        ScoLifecycleValidator.notifyStateChange(AudioRoutingTrace.ScoState.SCO_RELEASED)
        
        assertEquals(AudioRoutingTrace.ScoState.SCO_RELEASED, ScoLifecycleValidator.getCurrentState())
    }
}
