# 🚀 THAMIS Lab OS & LogPose

**Plataforma Autónoma de Entrenamiento, Simulación y Telemetría de IA Local**

---

## 📌 LogPose - Safe Riding Copilot

### Vision
LogPose is an intelligent riding copilot designed for motorcyclists and delivery riders. It enables safe interaction with mobile technology without the need to look at or touch the phone screen.

### Core Features (MVP)
- **Bluetooth Core**: Automatic connection to helmets and intercoms.
- **Background Engine**: Persistent foreground service for uninterrupted operation.
- **Voice Commands**: Hands-free control (Music, Volume, Navigation).
- **Music Control**: Full integration with major players (Spotify, YT Music).
- **Notification Intelligence**: Smart filtering and reading of important messages.
- **Safety Layer**: Automatic driving mode and attention management.

---

## 📌 THAMIS Lab OS Architecture
THAMIS Lab OS adopta una **Arquitectura de Inferencia Completa y Simulación Headless** construida en Kotlin puro con **Clean Architecture** y principios **SOLID**.

- **Capa de Dominio (`core:common`, `core:contracts`)**: Pure Kotlin, Zero-Framework, Explicit API.
- **Capa de Infraestructura**: Adaptadores de telemetría, simulación determinista y granjas físicas.

---

## 🛠️ Requisitos de Compilación
- **JDK**: 17 (LTS)
- **Kotlin**: 2.2+
- **Gradle**: 8.x + Kotlin DSL (`*.gradle.kts`)

---

## 🧪 Comandos Básicos

```bash
# Compilar y ejecutar pruebas unitarias de los módulos core
./gradlew test

# Verificar el compilado de core:common
./gradlew :core:common:build
```

---

## 📐 Convenciones de Código
- **Commits**: [Conventional Commits 1.0.0](docs/architecture/COMMIT_CONVENTIONS.md)
- **Ramas**: [Trunk-Based Development](docs/architecture/BRANCHING_CONVENTIONS.md)
- **Decisiones**: [ADRs en docs/architecture/adr/](docs/architecture/adr/)
