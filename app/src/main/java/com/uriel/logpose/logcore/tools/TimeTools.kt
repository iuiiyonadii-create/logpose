package com.uriel.logpose.logcore.tools

/**
 * Utilidades puras relacionadas con el tiempo de ejecución (no de calendario).
 *
 * Todas las funciones trabajan sobre [System.nanoTime] / [System.currentTimeMillis]
 * y no dependen de Android ni de ningún framework de UI.
 */
object TimeTools {

    /**
     * Devuelve el instante actual en milisegundos desde epoch (UTC).
     */
    fun now(): Long = System.currentTimeMillis()

    /**
     * Devuelve el instante actual en nanosegundos, apto solo para medir
     * intervalos relativos (no representa una fecha real).
     */
    fun nowNanos(): Long = System.nanoTime()

    /**
     * Calcula el tiempo transcurrido en milisegundos desde [startMillis] hasta ahora.
     */
    fun elapsed(startMillis: Long): Long = now() - startMillis

    /**
     * Calcula el tiempo transcurrido en nanosegundos desde [startNanos] hasta ahora.
     */
    fun elapsedNanos(startNanos: Long): Long = nowNanos() - startNanos

    /**
     * Ejecuta [block] y devuelve un par con el resultado y la duración en milisegundos.
     */
    inline fun <T> measure(block: () -> T): Pair<T, Long> {
        val start = System.nanoTime()
        val result = block()
        val durationMillis = (System.nanoTime() - start) / 1_000_000
        return result to durationMillis
    }

    /**
     * Ejecuta [block] y devuelve solamente la duración en milisegundos, descartando el resultado.
     */
    inline fun measureMillis(block: () -> Unit): Long {
        val start = System.nanoTime()
        block()
        return (System.nanoTime() - start) / 1_000_000
    }

    /**
     * Suspende el hilo actual durante [millis] milisegundos.
     *
     * No debe usarse en hilos que no puedan bloquearse (por ejemplo, el hilo principal de UI).
     * Para código suspendible, usar [CoroutineTools.delaySafe] en su lugar.
     */
    fun sleep(millis: Long) {
        if (millis <= 0) return
        Thread.sleep(millis)
    }
}
