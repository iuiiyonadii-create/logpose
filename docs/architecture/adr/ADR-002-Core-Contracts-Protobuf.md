# ADR-002: Core Contracts & Protobuf Message Schema

## Context
THAMIS Lab OS requires a strongly-typed, version-safe contract model for cross-module communication between all simulators, engines, analyzers, and the central orchestrator.

## Decision
1. **Pure Kotlin Contracts (`core:contracts`)**: Implemented immutable `data classes` and `sealed interfaces` (`LabEvent`, `LabCommand`, `LabResponse`, `CognitiveSnapshot`, `CognitiveDecision`, `CognitiveExplanation`).
2. **Zero Framework Dependencies**: `:core:contracts` depends only on `:core:common` and Kotlin Standard Library. Zero Android SDK or external UI dependencies.
3. **Protobuf Binary Schema (`lab_messages.proto`)**: Defined Protobuf schema for cross-process, disk storage, and network serialization with semantic versioning (`MessageVersion`).

## Consequences
- Guaranteed backward and forward compatibility across all current and future modules.
- Fast JVM unit testing and zero reflection overhead during execution.
