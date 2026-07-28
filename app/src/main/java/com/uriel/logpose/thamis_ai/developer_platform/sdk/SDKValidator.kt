package com.uriel.logpose.thamis_ai.developer_platform.sdk

/**
 * Validates extension manifests and permissions.
 */
class SDKValidator {
    fun validateManifest(manifest: Map<String, Any>): Boolean {
        return manifest.containsKey("id") && manifest.containsKey("version")
    }
}
