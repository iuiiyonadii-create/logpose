# ADR-004: Device Farm, ADB Manager & Hardware Telemetry Architecture

## Context
THAMIS Lab requires an orchestrator to manage physical Android devices, emulators, load-balance test scenarios, and record real-time hardware telemetry (CPU, RAM, GPU, Disk, Network) without coupling the core cognitive engine to Android SDK APIs.

## Decision
1. **Device Registry (`DeviceRegistry`) & Load Balancer (`ResourceScheduler`)**: Thread-safe registry for active physical devices and emulators, supporting Round-Robin load balancing across test execution nodes.
2. **ADB & Emulator Abstractions (`AdbManager`, `EmulatorManager`, `ApkManager`)**: Isolated command parsers for discovery, package deployment, logcat streaming, and emulator lifecycle control.
3. **Hardware Telemetry Engine (`HardwareTelemetryCollector` & `PerformanceAnalyzer`)**: Real-time metric collector and threshold analyzer evaluating CPU, RAM, GPU, Disk, and Network against system quality constraints.

## Consequences
- Complete isolation of Android ADB management from core cognitive logic.
- Real-time performance profiling during physical and emulator test runs.
