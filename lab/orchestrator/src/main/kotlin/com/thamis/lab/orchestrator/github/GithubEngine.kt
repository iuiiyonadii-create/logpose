package com.thamis.lab.orchestrator.github

import com.thamis.lab.core.common.logging.LabLogger

public data class ConventionalCommit(
    public val type: String, // feat, fix, docs, refactor, perf, test
    public val scope: String,
    public val description: String,
    public val formattedMessage: String
)

/**
 * GitHub Engine formatting Conventional Commits, generating release notes, changelogs, and release tags.
 */
public class GithubEngine {
    private val TAG = "GithubEngine"

    public fun createConventionalCommit(type: String, scope: String, description: String): ConventionalCommit {
        val msg = "$type($scope): $description"
        LabLogger.info(TAG, "Generated Conventional Commit: '$msg'")
        return ConventionalCommit(type, scope, description, msg)
    }

    public fun generateReleaseNotes(version: String, commits: List<ConventionalCommit>): String {
        val sb = StringBuilder("# Release $version\n\n")
        sb.append("## Changes in this Release\n")
        for (c in commits) {
            sb.append("- **${c.type}**(${c.scope}): ${c.description}\n")
        }
        return sb.toString()
    }
}
