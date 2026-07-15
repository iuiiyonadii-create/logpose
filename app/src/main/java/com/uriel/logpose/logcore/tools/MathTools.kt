package com.uriel.logpose.logcore.tools

/**
 * Utilidades matemáticas puras que complementan a `kotlin.math`.
 */
object MathTools {

    /** Devuelve el menor de dos valores. */
    fun min(a: Double, b: Double): Double = if (a < b) a else b

    /** Devuelve el mayor de dos valores. */
    fun max(a: Double, b: Double): Double = if (a > b) a else b

    /**
     * Interpola linealmente entre [start] y [end] según la fracción [fraction] (0.0 a 1.0).
     * [fraction] fuera de ese rango produce extrapolación.
     */
    fun interpolate(start: Double, end: Double, fraction: Double): Double {
        return start + (end - start) * fraction
    }

    /**
     * Normaliza [value] del rango [inMin, inMax] al rango [0, 1].
     * Devuelve `0.0` si [inMin] es igual a [inMax].
     */
    fun normalize(value: Double, inMin: Double, inMax: Double): Double {
        if (inMax == inMin) return 0.0
        return (value - inMin) / (inMax - inMin)
    }

    /**
     * Convierte [value] del rango [inMin, inMax] al rango [outMin, outMax].
     */
    fun mapRange(value: Double, inMin: Double, inMax: Double, outMin: Double, outMax: Double): Double {
        val normalized = normalize(value, inMin, inMax)
        return interpolate(outMin, outMax, normalized)
    }

    /** Calcula el máximo común divisor de [a] y [b]. */
    tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) kotlin.math.abs(a) else gcd(b, a % b)

    /** Calcula el mínimo común múltiplo de [a] y [b]. */
    fun lcm(a: Long, b: Long): Long {
        if (a == 0L || b == 0L) return 0L
        return kotlin.math.abs(a / gcd(a, b) * b)
    }
}
