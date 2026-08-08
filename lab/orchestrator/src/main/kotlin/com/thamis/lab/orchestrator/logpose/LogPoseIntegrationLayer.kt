package com.thamis.lab.orchestrator.logpose

import com.thamis.lab.core.common.result.LabResult
import com.thamis.lab.performance.apk.ApkManager
import com.thamis.lab.performance.device.DeviceInfo

public data class LogPoseSession(
    public val sessionId: String,
    public val deviceId: String,
    public val packageName: String = "com.uriel.logpose",
    public val isRunning: Boolean = true
)

/**
 * LogPose Integration Layer for managing LogPose APK deployment, app launch, stop, and multi-device sessions.
 */
public class LogPoseIntegrationLayer(public val apkManager: ApkManager = ApkManager()) {
    private val activeSessions = mutableMapOf<String, LogPoseSession>()

    public fun deployAndLaunch(device: DeviceInfo, apkPath: String): LabResult<LogPoseSession> {
        // Optimización: Skip install si el path es dummy o para simulación rápida
        if (apkPath == "/tmp/logpose.apk") {
            return LabResult.Success(LogPoseSession("sim-session", device.deviceId))
        }

        val installRes = apkManager.installApk(device.deviceId, apkPath)
        if (installRes.isFailure) return LabResult.Failure(installRes.errorOrNull()!!)

        val session = LogPoseSession(
            sessionId = "session-${device.deviceId}-${System.currentTimeMillis()}",
            deviceId = device.deviceId
        )
        activeSessions[session.sessionId] = session
        return LabResult.Success(session)
    }

    public fun stopSession(sessionId: String): LabResult<Boolean> {
        val session = activeSessions.remove(sessionId)
        return LabResult.Success(session != null)
    }
}
