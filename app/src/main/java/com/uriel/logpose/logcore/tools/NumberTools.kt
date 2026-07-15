package com.uriel.logpose.logcore.tools

import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * Utilidades puras para trabajar con números.
 */
object NumberTools {

    /** Limita [value] al rango [min, max]. */
    fun clamp(value: Int, min: Int, max: Int): Int = value.coerceIn(min, max)

    /** Limita [value] al rango [min, max]. */
    fun clamp(value: Double, min: Double, max: Double): Double = value.coerceIn(min, max)

    /** Limita [value] al rango [min, max]. */
    fun clamp(value: Long, min: Long, max: Long): Long = value.coerceIn(min, max)

    /** Redondea [value] a [decimals] posiciones decimales. */
    fun round(value: Double, decimals: Int = 0): Double {
        require(decimals >= 0) { "decimals no puede ser negativo" }
        val factor = Math.pow(10.0, decimals.toDouble())
        return (value * factor).roundToLong() / factor
    }

    /** Redondea [value] al entero más cercano. */
    fun roundToInt(value: Double): Int = value.roundToInt()

    /** Calcula el promedio de [values], o `0.0` si la lista está vacía. */
    fun average(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0
        return values.sum() / values.size
    }

    /** Calcula el porcentaje que representa [part] sobre [total]. Devuelve `0.0` si [total] es cero. */
    fun percentage(part: Double, total: Double): Double {
        if (total == 0.0) return 0.0
        return (part / total) * 100.0
    }

    /** Indica si [value] está dentro del rango [min, max] (inclusive). */
    fun isInRange(value: Double, min: Double, max: Double): Boolean = value in min..max

    /** Convierte [text] a [Double], o devuelve `null` si no es un número válido. */
    fun parseDoubleOrNull(text: String): Double? = text.toDoubleOrNull()

    /** Convierte [text] a [Int], o devuelve `null` si no es un número válido. */
    fun parseIntOrNull(text: String): Int? = text.toIntOrNull()
}
