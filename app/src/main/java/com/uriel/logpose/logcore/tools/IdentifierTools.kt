package com.uriel.logpose.logcore.tools

/**
 * Utilidades para validar y transformar identificadores de código (variables, claves, slugs).
 * Para generación de identificadores únicos, ver [IdGenerator] y [UuidTools].
 */
object IdentifierTools {

    private val KOTLIN_IDENTIFIER_REGEX = Regex("^[a-zA-Z_][a-zA-Z0-9_]*$")
    private val SLUG_REGEX = Regex("^[a-z0-9]+(-[a-z0-9]+)*$")

    /** Indica si [text] es un identificador válido de Kotlin (variable, función, clase). */
    fun isValidKotlinIdentifier(text: String): Boolean = KOTLIN_IDENTIFIER_REGEX.matches(text)

    /** Indica si [text] es un slug válido (minúsculas, dígitos y guiones simples). */
    fun isValidSlug(text: String): Boolean = SLUG_REGEX.matches(text)

    /** Convierte [text] en un slug: minúsculas, sin acentos, palabras separadas por guiones. */
    fun toSlug(text: String): String {
        val normalized = StringTools.removeAccents(text).lowercase()
        val slug = normalized.replace(Regex("[^a-z0-9]+"), "-").trim('-')
        return slug
    }

    /** Devuelve una versión de [text] apta como clave (sin espacios, minúsculas, separada por puntos). */
    fun toKey(text: String): String {
        return StringTools.removeAccents(text)
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), ".")
            .trim('.')
    }
}
