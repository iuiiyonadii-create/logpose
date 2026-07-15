package com.uriel.logpose.thamis.language

/**
 * Calcula una puntuación simple de similitud entre dos frases.
 *
 * Este motor irá evolucionando con el tiempo.
 */
object SimilarityEngine {

    fun score(a: String, b: String): Float {

        if (a == b) return 1f

        val wordsA = a.split(" ").toSet()
        val wordsB = b.split(" ").toSet()

        val common = wordsA.intersect(wordsB).size
        val total = wordsA.union(wordsB).size

        if (total == 0) return 0f

        return common.toFloat() / total.toFloat()
    }
}