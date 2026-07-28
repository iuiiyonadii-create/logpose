package com.uriel.logpose.thamis.journey.engine

import com.uriel.logpose.thamis.journey.model.*
import com.uriel.logpose.thamis.journey.state.JourneyStateMachine
import com.uriel.logpose.thamis.journey.security.JourneySafetyGate
import com.uriel.logpose.thamis.journey.audit.*
import com.uriel.logpose.thamis.journey.validation.*
import com.uriel.logpose.thamis.journey.memory.JourneyMemory

/**
 * JourneyEngine v1.0: Orquestador central del dominio Journey.
 */
object JourneyEngine {
    
    private var stateMachine = JourneyStateMachine()
    private var currentSession: JourneySession? = null
    private var gpsAvailable = false
    private var lastSpeed = 0f

    /**
     * Procesa un evento externo y evalúa transiciones.
     */
    fun processEvent(event: JourneyEvent, evidences: List<JourneyEvidence>) {
        val confidence = JourneyEvidenceEvaluator.evaluate(evidences)
        
        // Shadow Mode: Si la confianza es baja, registramos pero no actuamos
        if (confidence < 0.3f) return

        val transition = stateMachine.transition(event) ?: return
        
        // Validación de Seguridad
        if (!JourneySafetyGate.isTransitionSafe(transition, gpsAvailable, lastSpeed)) {
            return
        }

        // Ejecución de la lógica de negocio por estado
        handleTransition(transition, evidences, confidence)
    }

    private fun handleTransition(transition: JourneyTransition, evidences: List<JourneyEvidence>, confidence: Float) {
        when (transition.to) {
            JourneyState.READY -> {
                currentSession = JourneySession()
            }
            JourneyState.MOVING -> {
                if (currentSession == null) {
                    currentSession = JourneySession()
                }
            }
            JourneyState.FINISHED -> {
                currentSession?.let {
                    it.endTime = System.currentTimeMillis()
                    JourneyMemory.registerJourney(it)
                    JourneyValidationEngine.validate(it)
                }
                currentSession = null
            }
            else -> {}
        }

        // Registro de Auditoría (Trace)
        JourneyAudit.record(JourneyTrace(transition, evidences, lastSpeed, confidence))
    }

    fun updateGPSStatus(available: Boolean) { 
        gpsAvailable = available 
    }
    
    fun updateSpeed(speed: Float) { 
        lastSpeed = speed 
    }

    fun getCurrentState(): JourneyState = stateMachine.currentState
}
