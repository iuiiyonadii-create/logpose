package com.thamis.lab.performance.apk

import com.thamis.lab.core.common.result.LabResult
import com.thamis.lab.performance.adb.AdbManager

/**
 * Manages APK installation, uninstallation, and package deployment across devices.
 */
public class ApkManager(public val adbManager: AdbManager = AdbManager()) {

    public fun installApk(deviceId: String, apkPath: String): LabResult<Boolean> {
        val result = adbManager.simulateAdbCommand(deviceId, "install -r $apkPath")
        return if (result.isSuccess) {
            LabResult.Success(true)
        } else {
            LabResult.Failure(result.errorOrNull()!!)
        }
    }

    public fun uninstallPackage(deviceId: String, packageName: String): LabResult<Boolean> {
        val result = adbManager.simulateAdbCommand(deviceId, "uninstall $packageName")
        return if (result.isSuccess) {
            LabResult.Success(true)
        } else {
            LabResult.Failure(result.errorOrNull()!!)
        }
    }
}
