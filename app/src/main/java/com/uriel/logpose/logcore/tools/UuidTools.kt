package com.uriel.logpose.logcore.tools

import java.util.UUID

/**
 * Utilidades para generar y validar UUID (RFC 4122).
 */
object UuidTools {

    /** Genera un UUID v4 aleatorio como texto. */
    fun generate(): String = UUID.randomUUID().toString()

    /** Genera un UUID v4 aleatorio como [UUID]. */
    fun generateUuid(): UUID = UUID.randomUUID()

    /** Indica si [text] es un UUID válido. */
    fun isValid(text: String): Boolean {
        return try {
            UUID.fromString(text)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    /** Genera un UUID determinístico (v3, basado en MD5) a partir de [name] y un [namespace] opcional. */
    fun deterministic(name: String, namespace: String = ""): UUID {
        return UUID.nameUUIDFromBytes((namespace + name).toByteArray(Charsets.UTF_8))
    }

    /** Convierte [text] a [UUID], o devuelve `null` si no es válido. */
    fun parseOrNull(text: String): UUID? {
        return try {
            UUID.fromString(text)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}
