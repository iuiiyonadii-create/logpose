package com.thamis.lab.performance.command

import com.thamis.lab.core.common.logging.LabLogger
import com.thamis.lab.core.common.result.LabResult
import java.util.concurrent.CopyOnWriteArrayList

public data class CommandExecutionRecord(
    public val timestampMs: Long,
    public val targetSerial: String,
    public val commandString: String,
    public val durationMs: Long,
    public val exitCode: Int,
    public val stdout: String,
    public val stderr: String,
    public val isSuccess: Boolean
)

/**
 * Real ADB Command Runner executing system commands with full execution history, exit code, stdout, stderr, and latency tracking.
 */
public class RealCommandRunner {
    private val TAG = "RealCommandRunner"
    private val history = CopyOnWriteArrayList<CommandExecutionRecord>()

    public fun executeAdbCommand(targetSerial: String, vararg adbArgs: String): CommandExecutionRecord {
        val fullArgs = mutableListOf("adb", "-s", targetSerial)
        fullArgs.addAll(adbArgs)
        val cmdString = fullArgs.joinToString(" ")

        val startTime = System.currentTimeMillis()
        var exitCode = -1
        var stdout = ""
        var stderr = ""

        try {
            val process = ProcessBuilder(fullArgs).start()
            stdout = process.inputStream.bufferedReader().readText().trim()
            stderr = process.errorStream.bufferedReader().readText().trim()
            exitCode = process.waitFor()
        } catch (e: Exception) {
            stderr = e.message ?: "Process execution failed"
            LabLogger.error(TAG, "Failed command '$cmdString'", e)
        }

        val duration = System.currentTimeMillis() - startTime
        val record = CommandExecutionRecord(
            timestampMs = startTime,
            targetSerial = targetSerial,
            commandString = cmdString,
            durationMs = duration,
            exitCode = exitCode,
            stdout = stdout,
            stderr = stderr,
            isSuccess = exitCode == 0
        )

        history.add(record)
        LabLogger.info(TAG, "Command Record -> '$cmdString' [Exit: $exitCode, Latency: ${duration}ms, Success: ${record.isSuccess}]")

        return record
    }

    public fun openApp(serial: String, packageName: String = "com.uriel.logpose", activityName: String = ".MainActivity"): CommandExecutionRecord {
        return executeAdbCommand(serial, "shell", "am", "start", "-n", "$packageName/$activityName")
    }

    public fun closeApp(serial: String, packageName: String = "com.uriel.logpose"): CommandExecutionRecord {
        return executeAdbCommand(serial, "shell", "am", "force-stop", packageName)
    }

    public fun clearData(serial: String, packageName: String = "com.uriel.logpose"): CommandExecutionRecord {
        return executeAdbCommand(serial, "shell", "pm", "clear", packageName)
    }

    public fun inputTap(serial: String, x: Int, y: Int): CommandExecutionRecord {
        return executeAdbCommand(serial, "shell", "input", "tap", x.toString(), y.toString())
    }

    public fun inputText(serial: String, text: String): CommandExecutionRecord {
        return executeAdbCommand(serial, "shell", "input", "text", text)
    }

    public fun captureScreenshot(serial: String, remotePath: String = "/sdcard/screen.png"): CommandExecutionRecord {
        return executeAdbCommand(serial, "shell", "screencap", "-p", remotePath)
    }

    public fun runMonkeyTest(serial: String, packageName: String = "com.uriel.logpose", eventCount: Int = 100): CommandExecutionRecord {
        return executeAdbCommand(serial, "shell", "monkey", "-p", packageName, eventCount.toString())
    }

    public fun getCommandHistory(): List<CommandExecutionRecord> = history.toList()
    public fun clearHistory() { history.clear() }
}
