# ADR-007: Unified Simulation Orchestrator, LogPose Integration & Mission Control

## Context
THAMIS Lab requires a unified operational orchestrator (`:lab:orchestrator`) to execute full test campaigns across devices, control LogPose app lifecycle (deploy, launch, stop), integrate with AI Analysis and Quality engines, and expose state to the Mission Control UI (`:ui:mission-control`).

## Decision
1. **Unified Simulation Orchestrator (`UnifiedSimulationOrchestrator`)**: Operational core coordinating Device Discovery, LogPose Deployment, Campaign Execution, Quality Scoring, and Intelligence Report generation in a single automated pipeline.
2. **LogPose Integration Layer (`LogPoseIntegrationLayer`)**: Controls APK deployment and session lifecycle (`LogPoseSession`) without altering LogPose application code.
3. **Test Campaign Engine (`TestCampaignEngine`)**: Batch campaign runner evaluating multi-scenario test suites.
4. **Mission Control Controller (`MainDashboardController`)**: Reactive UI controller tracking active devices, total campaigns executed, and real-time quality scores.

## Consequences
- Full end-to-end automation: Device detection ➔ APK deployment ➔ Scenario execution ➔ Context simulation ➔ AI evaluation ➔ Intelligence report generation.
- Strict adherence to Clean Architecture dependency rules (UI depends on Orchestrator, zero circular dependencies).
