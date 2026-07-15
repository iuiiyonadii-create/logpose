package com.uriel.logpose.logcore.tools

/**
 * Utilidades de reflexión.
 *
 * Implementación basada únicamente en Java Reflection para evitar la
 * dependencia kotlin-reflect.
 */
object ReflectionTools {

    /** Devuelve el nombre simple de la clase de [instance]. */
    fun simpleClassName(instance: Any): String =
        instance.javaClass.simpleName

    /** Devuelve el nombre completo de la clase de [instance]. */
    fun qualifiedClassName(instance: Any): String =
        instance.javaClass.name

    /**
     * Lee el valor de un campo por nombre.
     * Devuelve null si no existe o no puede accederse.
     */
    fun getPropertyValue(instance: Any, propertyName: String): Any? {
        return try {
            val field = instance.javaClass.getDeclaredField(propertyName)
            field.isAccessible = true
            field.get(instance)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Devuelve los nombres de todos los campos declarados.
     */
    fun propertyNames(clazz: Class<*>): List<String> {
        return clazz.declaredFields.map { it.name }
    }

    /**
     * Indica si [instance] pertenece a [clazz].
     */
    fun isInstanceOf(instance: Any, clazz: Class<*>): Boolean {
        return clazz.isInstance(instance)
    }
}