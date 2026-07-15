package com.uriel.logpose.logcore.tools

import java.util.UUID

/**
 * Utilidades puras de validación de formatos comunes.
 */
object ValidationTools {

    private val EMAIL_REGEX = Regex(
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?" +
            "(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)+$"
    )

    private val PHONE_REGEX = Regex("^\\+?[0-9\\s\\-()]{6,20}$")

    /** Valida que [text] tenga formato de email. */
    fun email(text: String): Boolean = EMAIL_REGEX.matches(text)

    /** Valida que [text] tenga formato de URL bien formada (http/https). */
    fun url(text: String): Boolean {
        return try {
            val uri = java.net.URI(text)
            uri.scheme != null && (uri.scheme == "http" || uri.scheme == "https") && uri.host != null
        } catch (e: Exception) {
            false
        }
    }

    /** Valida que [text] tenga un formato de teléfono razonable (dígitos, espacios, +, -, paréntesis). */
    fun phone(text: String): Boolean = PHONE_REGEX.matches(text)

    /** Valida que [text] sea un UUID válido. */
    fun uuid(text: String): Boolean {
        return try {
            UUID.fromString(text)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    /** Valida que [text] sea un JSON bien formado (objeto o array). */
    fun json(text: String): Boolean = JsonTools.isValidJson(text)

    /** Valida que [text] no esté vacío ni compuesto solo por espacios en blanco. */
    fun notBlank(text: String): Boolean = text.isNotBlank()

    /** Valida que [text] tenga una longitud entre [min] y [max] (inclusive). */
    fun lengthInRange(text: String, min: Int, max: Int): Boolean = text.length in min..max
}
