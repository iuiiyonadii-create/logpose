package com.uriel.logpose.logcore.tools

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Utilidades para comprimir y descomprimir archivos en formato .zip.
 */
object ZipTools {

    /**
     * Comprime [sourceDir] (y todo su contenido recursivamente) en el archivo [destinationZip].
     * Devuelve `true` si la operación fue exitosa.
     */
    fun zip(sourceDir: File, destinationZip: File): Boolean {
        if (!sourceDir.exists() || !sourceDir.isDirectory) return false
        return try {
            destinationZip.parentFile?.let { FileTools.crearDirectorios(it) }
            ZipOutputStream(destinationZip.outputStream()).use { zipOut ->
                sourceDir.walkTopDown().filter { it.isFile }.forEach { file ->
                    val entryName = file.relativeTo(sourceDir).path.replace(File.separatorChar, '/')
                    zipOut.putNextEntry(ZipEntry(entryName))
                    file.inputStream().use { input -> StreamTools.copy(input, zipOut) }
                    zipOut.closeEntry()
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Descomprime [zipFile] dentro de [destinationDir], preservando la estructura de carpetas.
     * Devuelve `true` si la operación fue exitosa.
     */
    fun unzip(zipFile: File, destinationDir: File): Boolean {
        if (!zipFile.exists()) return false
        return try {
            FileTools.crearDirectorios(destinationDir)
            ZipFile(zipFile).use { zip ->
                val entries = zip.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    val outFile = File(destinationDir, entry.name)
                    if (!outFile.canonicalPath.startsWith(destinationDir.canonicalPath)) {
                        // Previene ataques de path traversal ("zip slip").
                        return false
                    }
                    if (entry.isDirectory) {
                        FileTools.crearDirectorios(outFile)
                    } else {
                        outFile.parentFile?.let { FileTools.crearDirectorios(it) }
                        zip.getInputStream(entry).use { input ->
                            outFile.outputStream().use { output -> StreamTools.copy(input, output) }
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Lista los nombres de las entradas contenidas en [zipFile], o lista vacía si no puede leerse.
     */
    fun listar(zipFile: File): List<String> {
        return try {
            ZipFile(zipFile).use { zip ->
                zip.entries().asSequence().map { it.name }.toList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
