package com.uriel.logpose.thamis.multiagent

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * FASE 27.3 — THAMIS LAB EXECUTION LAYER
 * Generador de plantillas de código Kotlin a nivel de sistema.
 */
object KotlinGenerator {

    data class KotlinPatch(
        val className: String,
        val packageName: String,
        val codeContent: String,
        val targetPath: String
    )

    /**
     * Genera la estructura de un parche o clase Kotlin a partir de una propuesta.
     */
    fun generateClass(className: String, packageName: String, functions: List<String>): KotlinPatch {
        LogPoseLogger.d("KotlinGenerator: Generando estructura para $className en $packageName")

        val sb = StringBuilder()
        sb.append("package $packageName\n\n")
        sb.append("import com.uriel.logpose.core.compat.core.LogPoseLogger\n\n")
        sb.append("/**\n * Generado automáticamente por THAMIS LAB KotlinGenerator.\n */\n")
        sb.append("class $className {\n")

        functions.forEach { fnName ->
            val cleanFn = fnName.replace(Regex("[^a-zA-Z0-9_]"), "")
            if (cleanFn.isNotBlank()) {
                sb.append("    fun $cleanFn() {\n")
                sb.append("        LogPoseLogger.d(\"$className: Ejecutando $cleanFn\")\n")
                sb.append("    }\n\n")
            }
        }
        sb.append("}\n")

        val targetPath = "app/src/main/java/${packageName.replace('.', '/')}/$className.kt"
        return KotlinPatch(className, packageName, sb.toString(), targetPath)
    }
}
