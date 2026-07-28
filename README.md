# LogPose - Safe Riding Copilot

## Vision
LogPose is an intelligent riding copilot designed for motorcyclists and delivery riders. It enables safe interaction with mobile technology without the need to look at or touch the phone screen.

## Core Features (MVP)
- **Bluetooth Core**: Automatic connection to helmets and intercoms.
- **Background Engine**: Persistent foreground service for uninterrupted operation.
- **Voice Commands**: Hands-free control (Music, Volume, Navigation).
- **Music Control**: Full integration with major players (Spotify, YT Music).
- **Notification Intelligence**: Smart filtering and reading of important messages.
- **Safety Layer**: Automatic driving mode and attention management.

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: Clean Architecture (MVVM)
- **Concurrency**: Coroutines & Flow
- **Persistence**: Room
- **Validation**: THAMIS LAB Intelligence Platform

## Project Structure
- `app/`: Android Application module.
- `core/`: Common utilities and base classes.
- `data/`: Repositories and data sources (local/network).
- `domain/`: Business logic and Use Cases.
- `presentation/`: UI components and ViewModels.
- `services/`: Android Services (Foreground, Voice, Bluetooth).
- `tests/`: Automated test suites.
