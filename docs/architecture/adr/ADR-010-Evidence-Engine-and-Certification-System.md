# ADR-010: Evidence Engine, Traceability & Certification System

## Context
THAMIS Lab OS v1.2 requires an objective, reproducible evidence system (`:lab:evidence`) to record execution artifacts, bind evidence items to Git commits and Android device metadata, archive historical campaign runs, export reports (Markdown, JSON, HTML), and determine official certification levels without assumptions.

## Decision
1. **Traceable Execution Evidence (`ExecutionEvidence`)**: Immutable evidence container generated per test execution containing UUID, timestamp, gitCommit, gitBranch, deviceId, androidApi, scenarioId, durationMs, qualityScore, cpuPercent, ramMb, status, and logTrace.
2. **Thread-Safe Campaign Archive (`CampaignArchive`)**: In-memory and disk archive storing evidence items queryable by UUID, commit, or timestamp.
3. **Evidence-Based Certification Engine (`CertificationEngine`)**: Evaluates pass rate, average quality score, and sample size to objectively assign certification levels (`READY_FOR_INTERNAL_TESTING`, `READY_FOR_CLOSED_BETA`, `READY_FOR_OPEN_BETA`, `READY_FOR_RELEASE_CANDIDATE`, `READY_FOR_PRODUCTION`).
4. **Report Exporters (`EvidenceReportExporter`)**: Formats structured evidence into Markdown, JSON, and HTML.

## Consequences
- Every execution produces immutable, reproducible evidence bound to Git commit hash and device ID.
- Zero subjective assumptions: Certification is calculated strictly from empirical evidence.
