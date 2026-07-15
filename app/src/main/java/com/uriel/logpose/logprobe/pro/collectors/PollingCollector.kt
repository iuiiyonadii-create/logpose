package com.uriel.logpose.logprobe.collectors

/**
 * Contrato para collectors que obtienen su estado por consulta activa
 * (Bluetooth, Media, Window). Se ejecutan dentro del scope de la sesion
 * activa; CollectorManager es responsable de cancelarlos al hacer Stop.
 */
interface PollingCollector {

    /** Arranca el polling. Debe ser no-op si ya esta corriendo. */
    suspend fun start()

    /** Detiene el polling y libera cualquier recurso (listeners, etc). */
    fun stop()

    fun isRunning(): Boolean
}
