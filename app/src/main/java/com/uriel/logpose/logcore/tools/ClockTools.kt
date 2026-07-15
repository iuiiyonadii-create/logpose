package com.uriel.logpose.logcore.tools

/**
 * Abstracción del reloj del sistema para facilitar tests deterministas.
 * El resto del módulo usa `System.currentTimeMillis()` directamente (ver [TimeTools]);
 * este objeto existe para código que necesita poder sustituir la fuente de tiempo.
 */
interface Clock {
    /** Devuelve el instante actual en milisegundos desde epoch. */
    fun millis(): Long
}

/**
 * Utilidades y relojes predefinidos.
 */
object ClockTools {

    /** Reloj respaldado por el reloj real del sistema. */
    val system: Clock = object : Clock {
        override fun millis(): Long = System.currentTimeMillis()
    }

    /** Crea un reloj fijo que siempre devuelve [fixedMillis]. Útil para tests. */
    fun fixed(fixedMillis: Long): Clock = object : Clock {
        override fun millis(): Long = fixedMillis
    }

    /** Crea un reloj que avanza manualmente a partir de [startMillis] mediante [advance]. */
    fun mutable(startMillis: Long): MutableClock = MutableClock(startMillis)

    /** Reloj mutable de uso exclusivo en tests, permite avanzar el tiempo manualmente. */
    class MutableClock(private var current: Long) : Clock {
        override fun millis(): Long = current

        /** Avanza el reloj [deltaMillis] milisegundos. */
        fun advance(deltaMillis: Long) {
            current += deltaMillis
        }
    }
}
