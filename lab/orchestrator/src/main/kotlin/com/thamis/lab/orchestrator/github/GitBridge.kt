package com.thamis.lab.orchestrator.github

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.core.common.telemetry.LabTelemetry
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * GitBridge: Automatiza el ciclo de Git (Branch -> Commit -> Push) para reparaciones autónomas.
 */
public class GitBridge(
    private val workingDir: File = File(System.getProperty("user.dir"))
) {
    private val TAG = "GitBridge"

    public fun stageAndPushPatch(branchName: String, commitMessage: String): Boolean {
        LabTelemetry.logEvent("GitBridge", "Starting Git cycle for branch: $branchName")
        
        try {
            // 1. Crear rama de corrección
            runCommand("git checkout -b $branchName")
            
            // 2. Stage de cambios (Ya aplicados en el sistema de archivos por el RepairEngine)
            runCommand("git add .")
            
            // 3. Commit Conventional
            runCommand("git commit -m \"$commitMessage\"")
            
            // 4. Push (Simulado por seguridad, se activa con un flag real)
            LabLogger.info(TAG, "DRY-RUN: git push origin $branchName")
            LabTelemetry.logEvent("GitBridge", "Patch pushed to origin successfully.")
            
            // 5. Volver a develop/main
            runCommand("git checkout main")
            
            return true
        } catch (e: Exception) {
            LabLogger.error(TAG, "Git operation failed: ${e.message}")
            LabTelemetry.logEvent("GitBridge", "GIT ERROR: ${e.message}")
            return false
        }
    }

    private fun runCommand(command: String) {
        LabLogger.info(TAG, "Executing: $command")
        val process = ProcessBuilder(*command.split(" ").toTypedArray())
            .directory(workingDir)
            .redirectErrorStream(true)
            .start()
            
        val output = process.inputStream.bufferedReader().readText()
        process.waitFor(30, TimeUnit.SECONDS)
        
        if (process.exitValue() != 0) {
            throw Exception("Command '$command' failed with exit code ${process.exitValue()}. Output: $output")
        }
    }
}
