package com.uriel.logpose.logcore.tools

import java.io.File
import java.nio.charset.Charset

/**
 * Utilidades de sistema de archivos usando `java.io.File`.
 * No depende de Android (Context, Uri, etc.); para acceso a `content://` u otros
 * esquemas de Android, usar [UriTools] en conjunto con este objeto.
 */
object FileTools {

    /**
     * Lee el contenido completo de [file] como texto. Devuelve `null` si el archivo no existe
     * o no se puede leer.
     */
    fun leer(file: File, charset: Charset = Charsets.UTF_8): String? {
        return try {
            if (!file.exists() || !file.isFile) null else file.readText(charset)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Escribe [content] en [file], sobrescribiendo su contenido previo.
     * Crea los directorios padre si no existen. Devuelve `true` si la operación fue exitosa.
     */
    fun escribir(file: File, content: String, charset: Charset = Charsets.UTF_8): Boolean {
        return try {
            file.parentFile?.let { crearDirectorios(it) }
            file.writeText(content, charset)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Agrega [content] al final de [file] sin sobrescribir el contenido existente.
     */
    fun agregar(file: File, content: String, charset: Charset = Charsets.UTF_8): Boolean {
        return try {
            file.parentFile?.let { crearDirectorios(it) }
            file.appendText(content, charset)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Copia [source] hacia [destination], sobrescribiendo si ya existe.
     * Devuelve `true` si la operación fue exitosa.
     */
    fun copiar(source: File, destination: File): Boolean {
        return try {
            destination.parentFile?.let { crearDirectorios(it) }
            source.copyTo(destination, overwrite = true)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Mueve [source] hacia [destination]. Devuelve `true` si la operación fue exitosa.
     */
    fun mover(source: File, destination: File): Boolean {
        return try {
            destination.parentFile?.let { crearDirectorios(it) }
            if (destination.exists()) destination.delete()
            val renamed = source.renameTo(destination)
            if (renamed) true else {
                val copied = copiar(source, destination)
                if (copied) source.delete() else copied
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Borra [file]. Si es un directorio, borra su contenido de forma recursiva.
     * Devuelve `true` si la operación fue exitosa.
     */
    fun borrar(file: File): Boolean {
        return try {
            if (!file.exists()) true else file.deleteRecursively()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Crea [directory] y todos sus directorios padre si no existen.
     * Devuelve `true` si el directorio existe al finalizar la operación.
     */
    fun crearDirectorios(directory: File): Boolean {
        return directory.exists() && directory.isDirectory || directory.mkdirs()
    }

    /**
     * Indica si [file] existe.
     */
    fun existe(file: File): Boolean = file.exists()

    /**
     * Devuelve el tamaño en bytes de [file], o `-1` si no existe.
     */
    fun tamano(file: File): Long = if (file.exists()) file.length() else -1L
}
