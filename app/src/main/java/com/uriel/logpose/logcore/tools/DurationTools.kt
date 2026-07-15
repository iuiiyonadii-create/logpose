package com.uriel.logpose.logcore.tools

import java.time.Duration

/**
 * Utilidades puras para trabajar con [Duration] y conversiones de tiempo.
 */
object DurationTools {

    /** Crea una [Duration] a partir de milisegundos. */
    fun ofMillis(millis: Long): Duration = Duration.ofMillis(millis)

    /** Crea una [Duration] a partir de segundos. */
    fun ofSeconds(seconds: Long): Duration = Duration.ofSeconds(seconds)

    /** Convierte una [Duration] a un texto legible por humanos, por ejemplo "1h 2m 3s". */
    fun humanReadable(duration: Duration): String {
        val totalSeconds = duration.seconds
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val parts = mutableListOf<String>()
        if (hours > 0) parts += "${hours}h"
        if (minutes > 0) parts += "${minutes}m"
        if (seconds > 0 || parts.isEmpty()) parts += "${seconds}s"
        return parts.joinToString(" ")
    }

    /** Suma dos duraciones. */
    fun add(a: Duration, b: Duration): Duration = a.plus(b)

    /** Resta [b] de [a], sin permitir resultados negativos (se trunca a cero). */
    fun subtract(a: Duration, b: Duration): Duration {
        val result = a.minus(b)
        return if (result.isNegative) Duration.ZERO else result
    }

    /** Indica si [duration] es mayor que [other]. */
    fun isLongerThan(duration: Duration, other: Duration): Boolean = duration > other

    /** Limita [duration] a un rango [min, max]. */
    fun clamp(duration: Duration, min: Duration, max: Duration): Duration {
        require(min <= max) { "min no puede ser mayor que max" }
        return when {
            duration < min -> min
            duration > max -> max
            else -> duration
        }
    }
}
