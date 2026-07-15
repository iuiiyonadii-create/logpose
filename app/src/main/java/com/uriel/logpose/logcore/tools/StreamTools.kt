package com.uriel.logpose.logcore.tools

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * Utilidades para trabajar con [InputStream] / [OutputStream] de forma segura.
 */
object StreamTools {

    private const val DEFAULT_BUFFER_SIZE = 8192

    /**
     * Lee completamente [input] y lo devuelve como [ByteArray]. Cierra el stream al finalizar.
     */
    fun readAllBytes(input: InputStream): ByteArray {
        input.use { stream ->
            val buffer = ByteArrayOutputStream()
            copy(stream, buffer)
            return buffer.toByteArray()
        }
    }

    /**
     * Copia todos los bytes de [input] a [output] usando un buffer de [bufferSize].
     * No cierra ninguno de los dos streams. Devuelve la cantidad de bytes copiados.
     */
    fun copy(input: InputStream, output: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long {
        val buffer = ByteArray(bufferSize)
        var total = 0L
        var read: Int
        while (input.read(buffer).also { read = it } != -1) {
            output.write(buffer, 0, read)
            total += read
        }
        return total
    }

    /**
     * Cierra [closeable] ignorando cualquier excepción producida al hacerlo.
     */
    fun closeQuietly(closeable: AutoCloseable?) {
        try {
            closeable?.close()
        } catch (e: Exception) {
            // Se ignora intencionalmente: cierre best-effort.
        }
    }

    /**
     * Lee [input] completamente y lo devuelve como texto usando UTF-8.
     */
    fun readAsText(input: InputStream): String {
        return readAllBytes(input).toString(Charsets.UTF_8)
    }
}
