package com.thamis.lab.performance.device

public enum class DeviceState {
    ONLINE,
    OFFLINE,
    BUSY,
    UNAUTHORIZED
}

public enum class ConnectionType {
    USB,
    WIFI,
    EMULATOR
}

public data class DeviceInfo(
    public val deviceId: String,
    public val modelName: String,
    public val isEmulator: Boolean,
    public val state: DeviceState = DeviceState.ONLINE,
    public val apiLevel: Int = 34,
    public val connectionType: ConnectionType = ConnectionType.USB,
    public val rawModel: String? = null,
    public val product: String? = null
) {
    public val displayName: String
        get() {
            val name = rawModel ?: modelName
            val conn = when (connectionType) {
                ConnectionType.USB -> "USB"
                ConnectionType.WIFI -> "WiFi"
                ConnectionType.EMULATOR -> "Emulator"
            }
            return "$name ($conn)"
        }

    public val adbTargetSerial: String get() = deviceId
}
