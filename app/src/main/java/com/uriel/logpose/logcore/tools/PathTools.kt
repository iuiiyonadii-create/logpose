package com.uriel.logpose.logcore.tools

import java.io.File

/**
 * Utilidades puras para manipulación de rutas de archivos, sin acceso a disco.
 */
object PathTools {

    /** Une [segments] en una sola ruta usando el separador del sistema. */
    fun join(vararg segments: String): String {
        return segments.filter { it.isNotEmpty() }
            .joinToString(File.separator)
            .replace(Regex("[/\\\\]+"), File.separator)
    }

    /** Devuelve la extensión de [path] (sin el punto), o cadena vacía si no tiene. */
    fun extension(path: String): String = File(path).extension

    /** Devuelve el nombre de archivo de [path], incluyendo la extensión. */
    fun fileName(path: String): String = File(path).name

    /** Devuelve el nombre de archivo de [path] sin la extensión. */
    fun fileNameWithoutExtension(path: String): String = File(path).nameWithoutExtension

    /** Devuelve el directorio padre de [path], o `null` si no tiene. */
    fun parent(path: String): String? = File(path).parent

    /** Normaliza [path] resolviendo segmentos `.` y `..` sin acceder al disco. */
    fun normalize(path: String): String = File(path).normalize().path

    /** Indica si [path] es una ruta absoluta. */
    fun isAbsolute(path: String): Boolean = File(path).isAbsolute

    /** Combina [base] con [relative] para obtener una ruta resultante. */
    fun resolve(base: String, relative: String): String = File(base, relative).path
}
