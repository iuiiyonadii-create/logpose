# Lab Discovery Autonomous System Implementation

Implement a robust, autonomous PC Bridge discovery system for THAMIS using UDP Broadcast/Multicast. This eliminates the need for static IP configuration and allows the Android device to find the Laboratory/PC environment automatically.

## User Review Required

> [!IMPORTANT]
> The system will use UDP port `5051` for discovery. Ensure this port is open on the host PC.

## Proposed Changes

### [Component: Core Network]

#### [MODIFY] [LabDiscoveryService.kt](file:///C:/projects/LogPose4/app/src/main/java/com/uriel/logpose/core/parser/LabDiscoveryService.kt)
- Refactor to provide a `StateFlow<String?>` for the current PC IP.
- Implement periodic heartbeat monitoring.
- Add support for discovering the bridge via UDP Broadcast.

#### [MODIFY] [LogPoseApplication.kt](file:///C:/projects/LogPose4/app/src/main/java/com/uriel/logpose/core/app/LogPoseApplication.kt)
- Ensure `LabDiscoveryService` is initialized and started correctly during app startup.

### [Component: Cognitive Pipeline]

#### [MODIFY] [CognitivePipeline.kt](file:///C:/projects/LogPose4/app/src/main/java/com/uriel/logpose/thamis/cognitive/CognitivePipeline.kt)
- Update `getUdpSender()` to react to IP changes from `LabDiscoveryService`.

### [Verification Tools]

#### [NEW] [simulate_pc_bridge.py](file:///C:/projects/LogPose4/.artifacts/a4a994c6-3eb2-4136-888d-fe1e9378de48/scratch/simulate_pc_bridge.py)
- A scratch script to simulate the PC side broadcasting the magic token.

## Verification Plan

### Manual Verification
- Run the `simulate_pc_bridge.py` on the host PC.
- Check Logcat for "📡 Discovery: Bridge detected at [IP]".
- Verify that `pc_ip` in `SettingsManager` is updated.
- Send a music command and verify it reaches the simulated bridge (if implemented in the script).
