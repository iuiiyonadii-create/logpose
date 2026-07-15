package com.uriel.logpose.logcore.tools

import java.util.concurrent.atomic.AtomicLong

/**
 * Generador de identificadores únicos dentro del proceso actual.
 * No garantiza unicidad entre procesos o dispositivos distintos: para eso usar [UuidTools].
 */
object IdGenerator {

    private val sequence = AtomicLong(0)

    /** Devuelve un contador incremental único durante la vida del proceso actual. */
    fun nextSequential(): Long = sequence.incrementAndGet()

    /** Genera un identificador corto (8 caracteres alfanuméricos) razonablemente único. */
    fun shortId(): String = RandomTools.alphanumeric(8)

    /**
     * Genera un identificador con [prefix], seguido de un guion bajo y un contador secuencial,
     * por ejemplo `prefix_1`, `prefix_2`.
     */
    fun withPrefix(prefix: String): String = "${prefix}_${nextSequential()}"

    /** Reinicia el contador secuencial interno a cero. Uso principal: tests. */
    fun resetSequence() {
        sequence.set(0)
    }
}
