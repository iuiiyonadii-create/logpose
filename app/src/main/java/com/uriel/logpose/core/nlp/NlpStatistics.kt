package com.uriel.logpose.core.nlp

object NlpStatistics {

    private var processed = 0

    fun register() {

        processed++
    }

    fun total(): Int =
        processed

    fun reset() {

        processed = 0
    }
}