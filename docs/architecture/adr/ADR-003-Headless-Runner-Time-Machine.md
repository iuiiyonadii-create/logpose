# ADR-003: Headless Runner & Deterministic Time Machine Architecture

## Context
THAMIS Lab requires a 100% deterministic, high-throughput simulation engine to execute millions of events without Android dependencies, with full rewind and replay capabilities.

## Decision
1. **Pure Kotlin Virtual Clock (`VirtualClock`)**: Manages simulation time independently of system wall-clock, supporting pause, resume, speed multiplier, and step control.
2. **Deterministic Priority Queue (`DeterministicEventQueue`)**: Enforces strict execution ordering based on `timestampMs` and `eventId`.
3. **Headless Runner (`HeadlessRunner`)**: High-throughput execution engine capable of running >10,000 events/second in a single JVM thread.
4. **Deterministic Time Machine (`DeterministicTimeMachine`)**: Combines state snapshots (`SnapshotManager`), state history store (`DeterministicStateStore`), and event replay (`ReplayEngine`) for exact state rewind and defect reproduction.

## Consequences
- Guaranteed absolute reproducibility (same seed + events = same result).
- Zero reflection and zero Android SDK pollution in simulation core.
