package com.uriel.logpose.logcore.tools

/**
 * Utilidades puras para trabajar con nombres de paquete Java/Kotlin (cadenas con puntos).
 * No consulta el classloader ni el sistema de paquetes de Android.
 */
object PackageTools {

    /** Devuelve el último segmento de [packageName] (por ejemplo, "tools" en "a.b.tools"). */
    fun lastSegment(packageName: String): String {
        return packageName.substringAfterLast('.', packageName)
    }

    /** Devuelve el paquete padre de [packageName], o `null` si no tiene puntos. */
    fun parent(packageName: String): String? {
        val index = packageName.lastIndexOf('.')
        return if (index == -1) null else packageName.substring(0, index)
    }

    /** Indica si [packageName] pertenece al árbol de [root] (es igual o subpaquete). */
    fun isSubPackageOf(packageName: String, root: String): Boolean {
        return packageName == root || packageName.startsWith("$root.")
    }

    /** Une segmentos de paquete con puntos, ignorando segmentos vacíos. */
    fun join(vararg segments: String): String {
        return segments.filter { it.isNotEmpty() }.joinToString(".")
    }

    /** Valida que [packageName] tenga un formato de paquete Java/Kotlin válido. */
    fun isValid(packageName: String): Boolean {
        if (packageName.isEmpty()) return false
        val identifierRegex = Regex("^[a-zA-Z_][a-zA-Z0-9_]*$")
        return packageName.split('.').all { identifierRegex.matches(it) }
    }
}
