package com.uriel.logpose.thamis.intelligence.actuation

import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Clase de prueba manual para validar el FileSystemActuator.
 */
object FileSystemActuatorManualTest {

    fun runTest() {
        LogPoseLogger.i("ThamisActuator: Iniciando prueba manual...")

        val sandboxPath = "C:/projects/LogPose4/app/src/main/java/com/uriel/logpose/thamis/generated/TestDataClass.kt"
        val content = """
            package com.uriel.logpose.thamis.generated
            
            /**
             * Data class generada automáticamente por THAMIS LAB.
             */
            data class TestDataClass(
                val id: Int,
                val message: String
            )
        """.trimIndent()

        // 1. Probar escritura de archivo nuevo
        val result1 = FileSystemActuator.writeFile(sandboxPath, content, overwrite = false)
        LogPoseLogger.i("ThamisActuator: Resultado Test 1 (Nuevo): $result1")

        // 2. Probar intento de sobreescritura sin permiso (debe fallar)
        val result2 = FileSystemActuator.writeFile(sandboxPath, content, overwrite = false)
        LogPoseLogger.i("ThamisActuator: Resultado Test 2 (Bloquear duplicado): $result2")

        // 3. Probar sobreescritura con backup
        val updatedContent = content + "\n// Actualizado con backup"
        val result3 = FileSystemActuator.writeFile(sandboxPath, updatedContent, overwrite = true)
        LogPoseLogger.i("ThamisActuator: Resultado Test 3 (Sobreescritura con backup): $result3")
        
        // 4. Probar violación de seguridad (Path Traversal)
        val result4 = FileSystemActuator.writeFile("C:/Windows/System32/hack.kt", "virus")
        LogPoseLogger.i("ThamisActuator: Resultado Test 4 (Violación de seguridad): $result4")
    }
}
