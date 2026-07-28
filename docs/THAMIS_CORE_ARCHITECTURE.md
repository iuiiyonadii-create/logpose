# THAMIS Core Intelligence Architecture

## 1. The Decision Pipeline
Every external stimulus goes through the following stages:
1. **Event Capture**: (e.g. `NotificationListener`, `BluetoothReceiver`).
2. **Intent Parsing**: `CommandParser` extracts the core request.
3. **Contextual Injection**: `ContextEngine` adds real-time state (Speed, App state).
4. **Safety Filter**: `SafetyEngine` validates if the action is prudent.
5. **Action Orchestration**: `ThamisOrchestrator` triggers the final module.

## 2. Event Bus (SharedFlow)
LogPose uses a centralized `EventBus` to decouple modules. Any component can subscribe to `LogPoseEvent` and react without direct dependencies.

## 3. Local Intelligence
Unlike cloud assistants, THAMIS processes intent and context 100% on-device to ensure privacy and offline reliability for riders in remote areas.
