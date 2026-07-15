package com.uriel.logpose.logprobe.storage

import android.content.Context
import com.uriel.logpose.logprobe.common.ProbeClock
import com.uriel.logpose.logprobe.common.ProbeConstants
import com.uriel.logpose.logprobe.exporters.JsonExporter
import com.uriel.logpose.logprobe.exporters.TextExporter
import com.uriel.logpose.logprobe.exporters.ZipExporter
import com.uriel.logpose.logprobe.models.ProbeReport
import java.io.File

/**
 * Almacenamiento moderno (scoped storage): usa el directorio de archivos
 * especifico de la app (getExternalFilesDir), que no requiere el permiso
 * WRITE_EXTERNAL_STORAGE y se elimina automaticamente al desinstalar
 * LogPose. No usa MediaStore porque estos archivos son de diagnostico
 * interno, no contenido multimedia para compartir con otras apps por
 * defecto; si se quiere compartir, se hace explicitamente via FileProvider
 * desde la UI (fuera del alcance de este archivo).
 */
class ProbeStorage(private val context: Context) {

    private fun exportDir(): File {
        val base = context.getExternalFilesDir(null) ?: context.filesDir
        val dir = File(base, ProbeConstants.EXPORT_DIR_NAME)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun writeJson(report: ProbeReport): File {
        val file = File(exportDir(), "${ProbeConstants.REPORT_FILE_PREFIX}${report.sessionId}.json")
        file.writeText(JsonExporter.export(report))
        return file
    }

    fun writeText(report: ProbeReport): File {
        val file = File(exportDir(), "${ProbeConstants.REPORT_FILE_PREFIX}${report.sessionId}.txt")
        file.writeText(TextExporter.export(report))
        return file
    }

    fun writeZip(report: ProbeReport): File {
        val json = writeJson(report)
        val text = writeText(report)
        val zipFile = File(exportDir(), "${ProbeConstants.ZIP_FILE_PREFIX}${report.sessionId}.zip")
        return ZipExporter.zip(listOf(json, text), zipFile)
    }

    fun clearAll() {
        exportDir().listFiles()?.forEach { it.delete() }
    }
}
