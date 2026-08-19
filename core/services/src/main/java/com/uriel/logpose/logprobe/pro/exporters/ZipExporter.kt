package com.uriel.logpose.logprobe.exporters

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ZipExporter {

    /**
     * Empaqueta los archivos ya generados (JSON + TXT) en un unico ZIP.
     * No agrega nada nuevo: solo comprime lo que ProbeStorage ya escribio.
     */
    fun zip(files: List<File>, destination: File): File {
        ZipOutputStream(destination.outputStream()).use { zipOut ->
            files.forEach { file ->
                if (!file.exists()) return@forEach
                zipOut.putNextEntry(ZipEntry(file.name))
                file.inputStream().use { it.copyTo(zipOut) }
                zipOut.closeEntry()
            }
        }
        return destination
    }
}
