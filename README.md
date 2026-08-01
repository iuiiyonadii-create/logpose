# 🚀 THAMIS Lab OS & LogPose

**Plataforma Autónoma de Entrenamiento, Simulación y Telemetría de IA Local**

---

## 📌 Arquitectura
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
