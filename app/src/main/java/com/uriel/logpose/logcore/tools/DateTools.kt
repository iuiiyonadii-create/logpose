package com.uriel.logpose.logcore.tools

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Utilidades puras de fecha y hora basadas en `java.time` (sin dependencias de Android).
 */
object DateTools {

    /** Formato ISO-8601 por defecto usado cuando no se especifica un patrón. */
    private val DEFAULT_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

    /**
     * Formatea [instant] usando [pattern] y la zona horaria [zoneId].
     * Si [pattern] es nulo, se usa formato ISO-8601.
     */
    fun format(instant: Instant, pattern: String? = null, zoneId: ZoneId = ZoneOffset.UTC): String {
        val zoned = instant.atZone(zoneId)
        val formatter = pattern?.let { DateTimeFormatter.ofPattern(it) } ?: DEFAULT_FORMATTER
        return zoned.format(formatter)
    }

    /**
     * Formatea un timestamp en milisegundos usando [pattern] y la zona horaria [zoneId].
     */
    fun formatMillis(epochMillis: Long, pattern: String? = null, zoneId: ZoneId = ZoneOffset.UTC): String {
        return format(Instant.ofEpochMilli(epochMillis), pattern, zoneId)
    }

    /**
     * Parsea [text] usando [pattern] y devuelve el [Instant] correspondiente,
     * o `null` si el texto no es parseable.
     */
    fun parse(text: String, pattern: String, zoneId: ZoneId = ZoneOffset.UTC): Instant? {
        return try {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            ZonedDateTime.parse(text, formatter.withZone(zoneId)).toInstant()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parsea [text] asumiendo formato ISO-8601, o devuelve `null` si falla.
     */
    fun parseIso(text: String): Instant? {
        return try {
            Instant.parse(text)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Convierte [instant] a la zona horaria indicada por [zoneId].
     */
    fun timezone(instant: Instant, zoneId: ZoneId): ZonedDateTime = instant.atZone(zoneId)

    /**
     * Devuelve la zona horaria por defecto del sistema.
     */
    fun systemTimezone(): ZoneId = ZoneId.systemDefault()

    /**
     * Indica si [text] es parseable con [pattern].
     */
    fun isValid(text: String, pattern: String): Boolean = parse(text, pattern) != null
}
