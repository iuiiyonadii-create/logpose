package com.uriel.logpose.thamis.release.versioning

import com.uriel.logpose.thamis.release.model.BetaVersion
import com.uriel.logpose.thamis.release.model.ReleaseStatus

/**
 * Controla el versionamiento y cambios del producto.
 */
object VersionManager {
    private val versions = mutableListOf<BetaVersion>()

    fun registerNewVersion(v: String, changes: List<String>) {
        versions.add(BetaVersion(v, changes = changes, status = ReleaseStatus.PUBLIC_BETA))
    }

    fun getCurrentVersion(): String = versions.lastOrNull()?.version ?: "v0.0.0-dev"

    fun getChangelog(): List<String> = versions.flatMap { it.changes }
}
