package com.uriel.logpose.logcore.tools

import java.io.File
import java.util.zip.CRC32
import java.util.zip.Adler32

/**
 * Utilidades de checksum para verificación de integridad de archivos y bytes.
 * Para hashing criptográfico, ver [HashTools].
 */
object ChecksumTools {

    /** Calcula el CRC32 de [bytes]. */
    fun crc32(bytes: ByteArray): Long {
        val crc = CRC32()
        crc.update(bytes)
        return crc.value
    }

    /** Calcula el CRC32 del contenido de [file]. Devuelve `-1` si el archivo no puede leerse. */
    fun crc32(file: File): Long {
        return try {
            crc32(file.readBytes())
        } catch (e: Exception) {
            -1L
        }
    }

    /** Calcula el checksum Adler-32 de [bytes]. */
    fun adler32(bytes: ByteArray): Long {
        val adler = Adler32()
        adler.update(bytes)
        return adler.value
    }

    /** Indica si [bytes] coincide con el checksum CRC32 esperado en [expected]. */
    fun matchesCrc32(bytes: ByteArray, expected: Long): Boolean = crc32(bytes) == expected
}
