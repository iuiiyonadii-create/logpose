package com.uriel.logpose.logprobe.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Unica fuente de tiempo de LogProbe. Evita que cada modulo llame
 * System.currentTimeMillis() por su cuenta y facilite testear con reloj fijo
 * si hiciera falta a futuro.
 */
object ProbeClock {

    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun nowMillis(): Long = System.currentTimeMillis()

    fun nowIso(): String = isoFormat.format(Date(nowMillis()))

    fun isoOf(millis: Long): String = isoFormat.format(Date(millis))
}
