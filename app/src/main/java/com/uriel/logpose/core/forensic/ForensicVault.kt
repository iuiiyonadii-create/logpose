package com.uriel.logpose.core.forensic

import android.content.Context
import com.uriel.logpose.core.compat.core.LogPoseLogger
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

/**
 * ForensicVault: Persistent storage for acoustic failures.
 * v70.0: Mandatory for Staff Engineering QA.
 */
object ForensicVault {

    private var vaultFile: File? = null

    fun initialize(context: Context) {
        val dir = context.getExternalFilesDir(null)
        vaultFile = File(dir, "forensic_blackbox.jsonl")
    }

    fun recordFailure(event: JSONObject) {
        val data = event.toString() + "\n"
        try {
            vaultFile?.let { file ->
                FileOutputStream(file, true).use { it.write(data.toByteArray()) }
            }
        } catch (e: Exception) {
            LogPoseLogger.e("ForensicVault", "Failed to record failure: ${e.message}")
        }
    }
}
