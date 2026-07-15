package com.uriel.logpose.logcore.tools

/**
 * Utilidades puras para trabajar con expresiones regulares.
 */
object RegexTools {

    /** Indica si [text] coincide completamente con [pattern]. */
    fun matches(text: String, pattern: String): Boolean = Regex(pattern).matches(text)

    /** Indica si [pattern] aparece en cualquier parte de [text]. */
    fun containsMatch(text: String, pattern: String): Boolean = Regex(pattern).containsMatchIn(text)

    /** Devuelve todas las coincidencias de [pattern] dentro de [text]. */
    fun findAll(text: String, pattern: String): List<String> {
        return Regex(pattern).findAll(text).map { it.value }.toList()
    }

    /** Devuelve el primer grupo de captura de la primera coincidencia, o `null` si no hay. */
    fun firstGroup(text: String, pattern: String, groupIndex: Int = 1): String? {
        val match = Regex(pattern).find(text) ?: return null
        return match.groupValues.getOrNull(groupIndex)
    }

    /** Reemplaza todas las coincidencias de [pattern] en [text] por [replacement]. */
    fun replaceAll(text: String, pattern: String, replacement: String): String {
        return Regex(pattern).replace(text, replacement)
    }

    /** Divide [text] usando [pattern] como separador. */
    fun split(text: String, pattern: String): List<String> = text.split(Regex(pattern))

    /** Escapa los caracteres especiales de [text] para poder usarlo como literal en un patrón. */
    fun escape(text: String): String = Regex.escape(text)
}
