package com.uriel.logpose.thamis.journey.model

/**
 * Estados de la máquina de estados de viaje THAMIS Journey v1.0.
 */
enum class JourneyState {
    OFF,         // Sistema inactivo
    PREPARING,   // Detectando pre-condiciones (BT conectado, etc)
    READY,       // Pre-condiciones listas, esperando movimiento
    MOVING,      // En movimiento continuo
    STOPPED,     // Detenido temporalmente (semáforo)
    PAUSED,      // Pausa manual o prolongada
    PARKED,      // Moto estacionada (evidencia de fin)
    FINISHED     // Viaje concluido
}
