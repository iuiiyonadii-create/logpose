package com.uriel.logpose.logcore.tools

import kotlin.reflect.KClass

/**
 * Utilidades sencillas relacionadas con clases, sin recurrir a reflexión pesada.
 * Para inspección de propiedades/miembros, ver [ReflectionTools].
 */
object ClassTools {

    /** Devuelve el nombre simple de [kClass], o `"Unknown"` si es anónima. */
    fun simpleName(kClass: KClass<*>): String = kClass.simpleName ?: "Unknown"

    /** Devuelve el nombre calificado de [kClass], o `null` si no está disponible. */
    fun qualifiedName(kClass: KClass<*>): String? = kClass.qualifiedName

    /** Indica si [kClass] es subclase de [other] (o la misma clase). */
    fun isSubclassOf(kClass: KClass<*>, other: KClass<*>): Boolean {
        return other.java.isAssignableFrom(kClass.java)
    }

    /** Indica si [kClass] representa una interfaz. */
    fun isInterface(kClass: KClass<*>): Boolean = kClass.java.isInterface

    /** Indica si [kClass] representa un enum. */
    fun isEnum(kClass: KClass<*>): Boolean = kClass.java.isEnum
}
