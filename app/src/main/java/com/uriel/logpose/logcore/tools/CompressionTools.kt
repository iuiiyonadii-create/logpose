package com.uriel.logpose.logcore.tools

import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterOutputStream

/**
 * Utilidades de compresión genérica de bytes usando DEFLATE.
 * Para compresión de archivos en formato .zip, ver [ZipTools].
 */
object CompressionTools {

    /** Comprime [bytes] usando DEFLATE con el nivel de compresión [level] (0-9). */
    fun compress(bytes: ByteArray, level: Int = Deflater.DEFAULT_COMPRESSION): ByteArray {
        val deflater = Deflater(level)
        val output = ByteArrayOutputStream()
        DeflaterOutputStream(output, deflater).use { it.write(bytes) }
        deflater.end()
        return output.toByteArray()
    }

    /** Descomprime [bytes] previamente comprimidos con [compress]. */
    fun decompress(bytes: ByteArray): ByteArray {
        val inflater = Inflater()
        val output = ByteArrayOutputStream()
        InflaterOutputStream(output, inflater).use { it.write(bytes) }
        inflater.end()
        return output.toByteArray()
    }

    /** Calcula el ratio de compresión (0.0 a 1.0) entre el tamaño original y el comprimido. */
    fun compressionRatio(originalSize: Int, compressedSize: Int): Double {
        if (originalSize == 0) return 0.0
        return 1.0 - (compressedSize.toDouble() / originalSize.toDouble())
    }
}
