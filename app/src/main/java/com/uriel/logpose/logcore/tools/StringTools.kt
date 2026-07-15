package com.uriel.logpose.logcore.tools

import java.text.Normalizer
import java.util.Locale

/**
 * Utilidades puras de manipulación de texto.
 */
object StringTools {

    /**
     * Normaliza [text]: recorta espacios, colapsa espacios múltiples en uno solo
     * y elimina acentos.
     */
    fun normalizar(text: String): String {
        val trimmedCollapsed = text.trim().replace(Regex("\\s+"), " ")
        return removeAccents(trimmedCollapsed)
    }

    /**
     * Capitaliza la primera letra de [text], dejando el resto sin modificar.
     */
    fun capitalizar(text: String): String {
        if (text.isEmpty()) return text
        return text.substring(0, 1).uppercase(Locale.ROOT) + text.substring(1)
    }

    /**
     * Convierte [text] a camelCase a partir de palabras separadas por espacios, guiones o guiones bajos.
     */
    fun camelCase(text: String): String {
        val words = splitWords(text)
        if (words.isEmpty()) return ""
        val first = words.first().lowercase(Locale.ROOT)
        val rest = words.drop(1).joinToString("") { capitalizar(it.lowercase(Locale.ROOT)) }
        return first + rest
    }

    /**
     * Convierte [text] a snake_case a partir de palabras separadas por espacios, guiones,
     * guiones bajos o límites camelCase.
     */
    fun snakeCase(text: String): String {
        val words = splitWords(text)
        return words.joinToString("_") { it.lowercase(Locale.ROOT) }
    }

    /**
     * Elimina espacios al inicio y al final de [text].
     */
    fun trim(text: String): String = text.trim()

    /**
     * Elimina acentos y diacríticos de [text], conservando el resto de caracteres.
     */
    fun removeAccents(text: String): String {
        val normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
        return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }

    /**
     * Compara dos strings de forma nula-segura (`null == null` es `true`).
     */
    fun safeEquals(a: String?, b: String?): Boolean {
        if (a == null || b == null) return a == b
        return a == b
    }

    /**
     * Indica si [text] contiene [query] ignorando mayúsculas/minúsculas.
     */
    fun containsIgnoreCase(text: String, query: String): Boolean {
        return text.contains(query, ignoreCase = true)
    }

    /**
     * Divide [text] en palabras usando espacios, guiones, guiones bajos
     * y transiciones minúscula-mayúscula (límites camelCase) como separadores.
     */
    private fun splitWords(text: String): List<String> {
        val withSpaces = text
            .replace(Regex("[_\\-]+"), " ")
            .replace(Regex("([a-z0-9])([A-Z])"), "$1 $2")
        return withSpaces.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    }
}
