package com.uriel.logpose.logcore.tools

/**
 * Utilidades puras para trabajar con [Map].
 */
object MapTools {

    /** Devuelve el valor asociado a [key], o [default] si no existe. */
    fun <K, V> getOrDefault(map: Map<K, V>, key: K, default: V): V {
        return map[key] ?: default
    }

    /** Invierte un mapa, usando los valores como claves y las claves como valores. */
    fun <K, V> invert(map: Map<K, V>): Map<V, K> {
        return map.entries.associate { (k, v) -> v to k }
    }

    /** Combina dos mapas; en caso de claves repetidas gana [override]. */
    fun <K, V> merge(base: Map<K, V>, override: Map<K, V>): Map<K, V> {
        return base + override
    }

    /** Filtra un mapa según un predicado sobre sus entradas. */
    fun <K, V> filter(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Map<K, V> {
        return map.filter(predicate)
    }

    /** Aplica [transform] a cada valor del mapa, conservando las claves. */
    fun <K, V, R> mapValues(map: Map<K, V>, transform: (V) -> R): Map<K, R> {
        return map.mapValues { (_, v) -> transform(v) }
    }

    /** Indica si el mapa está vacío o es `null`. */
    fun isNullOrEmpty(map: Map<*, *>?): Boolean = map.isNullOrEmpty()

    /** Devuelve un mapa mutable seguro para concurrencia (respaldado por [LinkedHashMap] sincronizado). */
    fun <K, V> synchronizedMap(): MutableMap<K, V> {
        return java.util.Collections.synchronizedMap(LinkedHashMap())
    }
}
