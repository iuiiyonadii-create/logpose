package com.uriel.logpose.logcore.tools

/**
 * Utilidades puras para trabajar con [List].
 */
object ListTools {

    /** Devuelve una nueva lista sin elementos duplicados, preservando el orden de aparición. */
    fun <T> distinctPreservingOrder(list: List<T>): List<T> = list.distinct()

    /** Devuelve el último elemento de [list], o `null` si está vacía. */
    fun <T> lastOrNull(list: List<T>): T? = list.lastOrNull()

    /** Devuelve el primer elemento de [list], o `null` si está vacía. */
    fun <T> firstOrNull(list: List<T>): T? = list.firstOrNull()

    /** Mueve el elemento en [fromIndex] a [toIndex], devolviendo una nueva lista. */
    fun <T> move(list: List<T>, fromIndex: Int, toIndex: Int): List<T> {
        require(fromIndex in list.indices) { "fromIndex fuera de rango" }
        require(toIndex in list.indices) { "toIndex fuera de rango" }
        val mutable = list.toMutableList()
        val item = mutable.removeAt(fromIndex)
        mutable.add(toIndex, item)
        return mutable
    }

    /** Devuelve una lista con [item] insertado en [index]. */
    fun <T> insert(list: List<T>, index: Int, item: T): List<T> {
        val mutable = list.toMutableList()
        mutable.add(index, item)
        return mutable
    }

    /** Reparte [list] en [count] partes lo más equilibradas posible. */
    fun <T> partition(list: List<T>, count: Int): List<List<T>> {
        require(count > 0) { "count debe ser mayor que 0" }
        if (list.isEmpty()) return List(count) { emptyList() }
        val size = kotlin.math.ceil(list.size / count.toDouble()).toInt().coerceAtLeast(1)
        return list.chunked(size)
    }

    /** Devuelve `true` si [list] está ordenada de forma ascendente según [comparator]. */
    fun <T> isSorted(list: List<T>, comparator: Comparator<T>): Boolean {
        for (i in 0 until list.size - 1) {
            if (comparator.compare(list[i], list[i + 1]) > 0) return false
        }
        return true
    }

    /** Devuelve una copia inmutable segura para exponer como API pública. */
    fun <T> immutableCopy(list: List<T>): List<T> = list.toList()
}
