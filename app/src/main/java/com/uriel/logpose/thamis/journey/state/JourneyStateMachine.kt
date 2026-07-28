package com.uriel.logpose.thamis.journey.state

import com.uriel.logpose.thamis.journey.model.JourneyState
import com.uriel.logpose.thamis.journey.model.JourneyEvent
import com.uriel.logpose.thamis.journey.model.JourneyTransition

/**
 * Motor de estados puro (Pure Kotlin) para la gestión del viaje.
 * Solo maneja la lógica de transición, sin dependencias externas.
 */
class JourneyStateMachine(initialState: JourneyState = JourneyState.OFF) {
    
    var currentState: JourneyState = initialState
        private set

    /**
     * Evalúa un evento y devuelve la transición resultante si existe.
     */
    fun transition(event: JourneyEvent): JourneyTransition? {
        val nextState = when (currentState) {
            JourneyState.OFF -> when (event) {
                is JourneyEvent.BluetoothConnected -> JourneyState.PREPARING
                is JourneyEvent.ManualStart -> JourneyState.READY
                else -> currentState
            }
            
            JourneyState.PREPARING -> when (event) {
                is JourneyEvent.BluetoothDisconnected -> JourneyState.OFF
                is JourneyEvent.HelmetConnected -> JourneyState.READY
                is JourneyEvent.ManualStart -> JourneyState.READY
                else -> currentState
            }
            
            JourneyState.READY -> when (event) {
                is JourneyEvent.SpeedChanged -> if (event.speed > 5f) JourneyState.MOVING else currentState
                is JourneyEvent.MovementDetected -> if (event.isMoving) JourneyState.MOVING else currentState
                is JourneyEvent.BluetoothDisconnected -> JourneyState.OFF
                is JourneyEvent.ManualStop -> JourneyState.FINISHED
                else -> currentState
            }
            
            JourneyState.MOVING -> when (event) {
                is JourneyEvent.SpeedChanged -> if (event.speed < 1f) JourneyState.STOPPED else currentState
                is JourneyEvent.MovementDetected -> if (!event.isMoving) JourneyState.STOPPED else currentState
                is JourneyEvent.GPSLost -> JourneyState.PAUSED
                is JourneyEvent.ManualStop -> JourneyState.FINISHED
                else -> currentState
            }
            
            JourneyState.STOPPED -> when (event) {
                is JourneyEvent.SpeedChanged -> if (event.speed > 5f) JourneyState.MOVING else currentState
                is JourneyEvent.MovementDetected -> if (event.isMoving) JourneyState.MOVING else currentState
                is JourneyEvent.IdleTimeout -> JourneyState.PARKED
                is JourneyEvent.ManualStop -> JourneyState.FINISHED
                else -> currentState
            }
            
            JourneyState.PAUSED -> when (event) {
                is JourneyEvent.GPSRestored -> JourneyState.MOVING
                is JourneyEvent.ManualStart -> JourneyState.MOVING
                is JourneyEvent.ManualStop -> JourneyState.FINISHED
                else -> currentState
            }
            
            JourneyState.PARKED -> when (event) {
                is JourneyEvent.MovementDetected -> if (event.isMoving) JourneyState.MOVING else currentState
                is JourneyEvent.BluetoothDisconnected -> JourneyState.FINISHED
                is JourneyEvent.ManualStop -> JourneyState.FINISHED
                else -> currentState
            }
            
            JourneyState.FINISHED -> when (event) {
                is JourneyEvent.BluetoothDisconnected -> JourneyState.OFF
                is JourneyEvent.ManualStart -> JourneyState.READY
                else -> currentState
            }
        }

        return if (nextState != currentState) {
            val transition = JourneyTransition(
                from = currentState, 
                to = nextState, 
                trigger = event, 
                reason = "Transición gatillada por ${event::class.simpleName}"
            )
            currentState = nextState
            transition
        } else {
            null
        }
    }
}
