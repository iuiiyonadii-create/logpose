package com.uriel.logpose.thamis.intelligence.actuation

import com.uriel.logpose.core.compat.core.LogPoseLogger
import java.io.File
import java.nio.file.Paths

/**
 * FASE 1 — FILE SYSTEM ACTUATOR
 * Módulo de escritura segura de archivos generados por THAMIS.
 */
object FileSystemActuator {

    private const val TAG = "ThamisActuator"
    
    /**
     * Resuelve la raíz del proyecto de forma dinámica basada en el directorio de ejecución.
     */
    private val PROJECT_ROOT: File by lazy {
        val currentDir = System.getProperty("user.dir") ?: "."
        // Subimos niveles si estamos dentro de /app para llegar a la raíz LogPose4
        var root = File(currentDir)
        while (root.name == "app" || root.name == "java") {
            root = root.parentFile ?: root
        }
        root.absoluteFile
    }

    private val affectedFiles = mutableListOf<String>()

    /**
     * Escribe un archivo en la ruta especificada con validaciones de seguridad.
     */
    fun writeFile(path: String, content: String, overwrite: Boolean = false): ActuationResult {
        val targetFile = File(path).absoluteFile
        
        LogPoseLogger.i("$TAG: Intentando escribir en ${targetFile.path}")

        // 1. Validación de Seguridad: Path Traversal
        if (!targetFile.path.startsWith(PROJECT_ROOT.absolutePath)) {
            val error = "VIOLACIÓN DE SEGURIDAD: Intento de escritura fuera del proyecto."
            LogPoseLogger.e("$TAG: $error Path: ${targetFile.path}")
            return ActuationResult.Failure(path, error, FailureType.SECURITY_VIOLATION)
        }

        // 2. Validación de Contenido: Sintaxis mínima Kotlin
        if (!path.endsWith(".kt")) {
            val error = "VALIDACIÓN FALLIDA: Solo se permiten archivos .kt"
            return ActuationResult.Failure(path, error, FailureType.VALIDATION_ERROR)
        }
        if (content.isBlank()) {
            val error = "VALIDACIÓN FALLIDA: El contenido está vacío."
            return ActuationResult.Failure(path, error, FailureType.VALIDATION_ERROR)
        }
        
        // Validación de contenido binario (básico)
        if (content.contains('\u0000')) {
            val error = "VALIDACIÓN FALLIDA: El contenido parece ser binario."
            return ActuationResult.Failure(path, error, FailureType.VALIDATION_ERROR)
        }

        // 3. Gestión de Archivo Existente y Overwrite
        val exists = targetFile.exists()
        if (exists && !overwrite) {
            val error = "EL ARCHIVO YA EXISTE: Usa overwrite=true para reemplazarlo."
            LogPoseLogger.w("$TAG: $error")
            return ActuationResult.Failure(path, error, FailureType.FILE_EXISTS)
        }

        try {
            // Crear directorios si no existen
            targetFile.parentFile?.mkdirs()

            if (exists && overwrite) {
                // Hacer backup
                val backupFile = File(targetFile.path + ".bak")
                targetFile.copyTo(backupFile, overwrite = true)
                LogPoseLogger.i("$TAG: Backup creado en ${backupFile.name}")
            }

            targetFile.writeText(content)
            affectedFiles.add(targetFile.path)
            
            val type = if (exists) ActuationType.OVERWRITE else ActuationType.NEW_FILE
            LogPoseLogger.i("$TAG: ESCRITURA EXITOSA ($type) en ${targetFile.name}")
            
            return ActuationResult.Success(targetFile.path, type)
            
        } catch (e: Exception) {
            val error = "ERROR DE E/S: ${e.message}"
            LogPoseLogger.e("$TAG: $error")
            return ActuationResult.Failure(path, error, FailureType.IO_ERROR)
        }
    }

    /**
     * Genera un diff visual simple entre el archivo actual y el nuevo contenido.
     */
    fun previewDiff(path: String, newContent: String): String {
        val targetFile = File(path)
        if (!targetFile.exists()) return "Archivo nuevo:\n$newContent"
        
        val oldContent = targetFile.readText()
        val oldLines = oldContent.lines()
        val newLines = newContent.lines()
        
        val diff = StringBuilder()
        diff.append("--- PREVIEW DIFF (${targetFile.name}) ---\n")
        
        // Simulación de diff simple (comparación de líneas)
        val maxLines = maxOf(oldLines.size, newLines.size)
        for (i in 0 until maxLines) {
            val old = oldLines.getOrNull(i)
            val new = newLines.getOrNull(i)
            
            if (old != new) {
                if (old != null) diff.append("- $old\n")
                if (new != null) diff.append("+ $new\n")
            } else {
                diff.append("  $old\n")
            }
        }
        return diff.toString()
    }

    /**
     * Lista los archivos modificados en la sesión actual.
     */
    fun listAffectedFiles(): List<String> = affectedFiles.toList()
}
