# LogPose & THAMIS LAB Architecture

## 1. Modular Structure (Clean Architecture)
LogPose is built using a strict modular approach to ensure scalability and testability, validated by the THAMIS LAB platform.

- **`core`**: Contains system-wide logic, event bus, security, and global constants.
- **`domain`**: The business logic. Contains pure Kotlin models, repository interfaces, and use cases. No Android dependencies.
- **`data`**: Implementation of repositories. Handles Bluetooth APIs, Room database, DataStore, and external service listeners.
- **`presentation`**: UI layer using Jetpack Compose and MVVM. ViewModels observe `StateFlow` from repositories.
- **`services`**: Foreground services for background persistence and system-wide orchestration.

## 2. Intelligence Layer: THAMIS Core
THAMIS is an 18-layer intelligence platform that orchestrates LogPose's behavior.

### 2.1 The Event Pipeline
1. **Source**: An event occurs (e.g., Message received, Voice command, Navigation turn).
2. **Context Analyzer**: Evaluates the current state (Speed, Attention level, Music state).
3. **Safety Engine**: Checks if the action is safe at this moment.
4. **Decision Engine**: Determines the outcome (ALLOW, DELAY, IGNORE, BLOCK).
5. **Action Executor**: Performs the physical action (Voice output, Music pause).

### 2.2 Security Model (Zero Trust)
- No module is trusted by default.
- Every cross-module request through the `EventBus` is validated for permission and context.
- Local-first memory: Conversational data is never stored; only authorized preferences are kept in encrypted local storage.

## 3. Persistent Operation
LogPose utilizes a Foreground Service (`LogPoseService`) to ensure the "Copilot" remains active even when the screen is off or the app is in the background.

## 4. Voice-First UX
The UI is primarily for initial configuration. All interactions during riding are designed for Voice Input (STT) and Voice Output (TTS) through Bluetooth SCO channels.
