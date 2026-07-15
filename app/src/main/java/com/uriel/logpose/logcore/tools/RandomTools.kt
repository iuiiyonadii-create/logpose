package com.uriel.logpose.logcore.tools

import kotlin.random.Random

/**
 * Utilidades puras para generación de valores aleatorios.
 * Todas las funciones aceptan una fuente [Random] opcional para facilitar el testing
 * (por ejemplo, `Random(seed)` para resultados determinísticos).
 */
object RandomTools {

    /** Devuelve un entero aleatorio en el rango [min, max) usando [random]. */
    fun int(min: Int, max: Int, random: Random = Random.Default): Int {
        require(min < max) { "min debe ser menor que max" }
        return random.nextInt(min, max)
    }

    /** Devuelve un [Double] aleatorio en el rango [min, max) usando [random]. */
    fun double(min: Double, max: Double, random: Random = Random.Default): Double {
        require(min < max) { "min debe ser menor que max" }
        return min + random.nextDouble() * (max - min)
    }

    /** Devuelve un [Boolean] aleatorio usando [random]. */
    fun boolean(random: Random = Random.Default): Boolean = random.nextBoolean()

    /** Devuelve un elemento aleatorio de [list], o `null` si está vacía. */
    fun <T> pick(list: List<T>, random: Random = Random.Default): T? {
        if (list.isEmpty()) return null
        return list[random.nextInt(list.size)]
    }

    /** Devuelve una copia de [list] con sus elementos en orden aleatorio. */
    fun <T> shuffle(list: List<T>, random: Random = Random.Default): List<T> = list.shuffled(random)

    /** Genera una cadena alfanumérica aleatoria de longitud [length]. */
    fun alphanumeric(length: Int, random: Random = Random.Default): String {
        require(length >= 0) { "length no puede ser negativo" }
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length).map { chars[random.nextInt(chars.length)] }.joinToString("")
    }
}
