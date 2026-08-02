package com.thamis.lab.performance.logpose

public data class RealLogposeStatus(
    public val packageName: String = "com.uriel.logpose",
    public val isInstalled: Boolean = false,
    public val versionName: String = "Unknown",
    public val pid: Int = -1,
    public val isForeground: Boolean = false,
    public val ramUsedMb: Double = 0.0,
    public val cpuPercent: Double = 0.0,
    public val batteryPercent: Int = 100,
    public val audioPermissionGranted: Boolean = false,
    public val gpsPermissionGranted: Boolean = false,
    public val bluetoothPermissionGranted: Boolean = false,
    public val activeTimeSeconds: Long = 0L,
    public val lastCommandText: String = "--",
    public val lastErrorText: String = "None"
)
