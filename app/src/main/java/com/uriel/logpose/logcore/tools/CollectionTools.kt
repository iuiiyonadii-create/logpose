package com.uriel.logpose.logcore.tools

/**
 * Utilidades puras genéricas para colecciones ([Iterable] / [Collection]).
 * Para utilidades específicas de [List] o [Map], ver [ListTools] y [MapTools].
 */
object CollectionTools {

    /**
     * Devuelve el elemento en [index] de [collection], o `null` si el índice está fuera de rango.
     */
    fun <T> safeGet(collection: List<T>, index: Int): T? {
        return if (index in collection.indices) collection[index] else null
    }

    /**
     * Filtra los elementos nulos de [collection], devolviendo una lista de no-nulos.
     */
    fun <T : Any> filterNotNull(collection: Iterable<T?>): List<T> {
        return collection.filterNotNull()
    }

    /**
     * Agrupa los elementos de [collection] según la clave devuelta por [keySelector].
     */
    fun <T, K> group(collection: Iterable<T>, keySelector: (T) -> K): Map<K, List<T>> {
        return collection.groupBy(keySelector)
    }

    /**
     * Indexa los elementos de [collection] por la clave devuelta por [keySelector].
     * Si hay claves repetidas, se conserva el último elemento encontrado.
     */
    fun <T, K> index(collection: Iterable<T>, keySelector: (T) -> K): Map<K, T> {
        return collection.associateBy(keySelector)
    }

    /**
     * Indica si [collection] está vacía o es `null`.
     */
    fun isNullOrEmpty(collection: Collection<*>?): Boolean = collection.isNullOrEmpty()

    /**
     * Divide [collection] en sublistas de tamaño [size].
     */
    fun <T> chunk(collection: Iterable<T>, size: Int): List<List<T>> {
        require(size > 0) { "size debe ser mayor que 0" }
        return collection.chunked(size)
    }

    /**
     * Devuelve los elementos que aparecen en ambas colecciones.
     */
    fun <T> intersect(a: Iterable<T>, b: Iterable<T>): List<T> {
        val setB = b.toSet()
        return a.filter { it in setB }.distinct()
    }
}
