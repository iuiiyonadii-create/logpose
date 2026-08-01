package com.thamis.lab.core.contracts.version

/**
 * Message versioning contract for Protobuf and DTO compatibility.
 */
public data class MessageVersion(
    public val major: Int = 1,
    public val minor: Int = 0,
    public val patch: Int = 0
) {
    public val versionString: String get() = "$major.$minor.$patch"

    public fun isCompatibleWith(other: MessageVersion): Boolean {
        return this.major == other.major
    }
}
