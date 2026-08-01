# ADR-005: Scenario Simulation Engine, Environment Simulators & Fault Injection

## Context
THAMIS Lab requires a high-performance simulation engine capable of constructing reproducible scenarios, simulating real-world environmental context (Bluetooth, GPS speed, audio playback, low battery, calls, notifications), injecting deterministic/random faults, and validating outcomes against expected cognitive decisions.

## Decision
1. **Scenario Model & Fluent DSL (`Scenario`, `ScenarioBuilder`, `ScenarioRepository`)**: Immutable scenario container with fluent builder DSL and thread-safe repository storage.
2. **Environment Simulators (`EnvironmentSimulator`)**: Facade isolating physical and system state modifications (Bluetooth intercom, GPS speed, active app, audio focus, battery drop) without Android SDK bindings.
3. **Fault Injection Engine (`FaultInjectionEngine` & `FaultType`)**: Deterministic and seed-based random fault injection (Bluetooth drop, GPS loss, network disconnect, STT noise corruption).
4. **Validation & Assertion Engine (`ValidationEngine`)**: Automated evaluator matching actual vs expected cognitive decisions, intent match rates, and execution status.

## Consequences
- Full capability to simulate multi-device environmental conditions and edge cases in pure JVM (<10ms per scenario).
- Zero reflection and zero Android SDK dependency in core simulation logic.
