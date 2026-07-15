package com.uriel.logpose.logcore.tools

/**
 * Utilidades simples de medición de rendimiento en desarrollo.
 * No reemplaza a un profiler ni a un framework de micro-benchmarking formal.
 */
object BenchmarkTools {

    /**
     * Ejecuta [block] una vez y devuelve la duración en milisegundos.
     */
    inline fun measureExecution(block: () -> Unit): Long {
        val start = System.nanoTime()
        block()
        return (System.nanoTime() - start) / 1_000_000
    }

    /**
     * Ejecuta [block] [iterations] veces y devuelve la duración promedio en milisegundos.
     */
    inline fun measureAverage(iterations: Int, block: () -> Unit): Double {
        require(iterations > 0) { "iterations debe ser mayor que 0" }
        var total = 0L
        repeat(iterations) {
            total += measureExecution(block)
        }
        return total.toDouble() / iterations
    }

    /**
     * Ejecuta [block] [iterations] veces y devuelve todas las duraciones individuales en milisegundos.
     */
    inline fun measureAll(iterations: Int, block: () -> Unit): List<Long> {
        require(iterations > 0) { "iterations debe ser mayor que 0" }
        return (1..iterations).map { measureExecution(block) }
    }
}
