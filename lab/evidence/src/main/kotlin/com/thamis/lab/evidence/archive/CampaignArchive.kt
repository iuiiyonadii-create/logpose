package com.thamis.lab.evidence.archive

import com.thamis.lab.evidence.model.ExecutionEvidence
import java.util.concurrent.ConcurrentHashMap

/**
 * Thread-safe archive for campaign evidence logs and historical metrics.
 */
public class CampaignArchive {
    private val archive = ConcurrentHashMap<String, ExecutionEvidence>()

    public fun archiveEvidence(evidence: ExecutionEvidence) {
        archive[evidence.evidenceUuid] = evidence
    }

    public fun getEvidenceByUuid(uuid: String): ExecutionEvidence? {
        return archive[uuid]
    }

    public fun getEvidencesForCommit(gitCommit: String): List<ExecutionEvidence> {
        return archive.values.filter { it.gitCommit == gitCommit }
    }

    public fun getAllArchivedEvidences(): List<ExecutionEvidence> {
        return archive.values.sortedByDescending { it.timestampMs }
    }

    public fun clear() {
        archive.clear()
    }
}
