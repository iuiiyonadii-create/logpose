# THAMIS MVP - PLANO TÉCNICO Y ORDEN DE IMPLEMENTACIÓN

## 1. OBJETIVO DEL MVP
Lograr el primer ciclo cerrado de ingeniería: 
`START -> EMULATE -> TEST -> LOG -> REPORT`.

## 2. ESTRUCTURA DE ARCHIVOS (PYTHON)
```text
THAMIS/
├── core/
│   ├── orchestrator.py    # Director de la misión
│   └── event_bus.py       # Comunicación interna
├── emulator/
│   ├── manager.py         # Control de AVDs
│   └── adb_controller.py  # Comandos ADB
├── simulation/
│   └── scenario_engine.py # Lógica de TSF (Escenarios)
├── analyzer/
│   └── log_analyzer.py    # Detector de crashes
└── knowledge/
    └── database.py        # SQLite de errores
```

## 3. ORDEN DE IMPLEMENTACIÓN (DÍA A DÍA)

### DÍA 1: FOUNDATION & PROJECT SETUP
- [x] Estructura de carpetas Clean Architecture.
- [x] Configuración de entorno (TLDE).
- [x] Sistema de logs y manejo de errores.

### DÍA 2: CORE & EMULATOR MANAGER
- [x] Orchestrator básico y Event Bus.
- [x] Integración ADB y control de AVDs.
- [x] Instalación automática de APK.

### DÍA 3: SCENARIO ENGINE & TEST RUNNER
- [x] Definición de formato TSF (JSON).
- [x] Motor de ejecución de eventos secuenciales.
- [x] Captura de métricas iniciales (CPU/RAM).

### DÍA 4: LOG ANALYZER & KNOWLEDGE
- [x] Base de datos SQLite (TKGM).
- [x] Detector de Crashes y Causa Raíz básico.
- [x] Generador de reportes Markdown.

### SEMANA 1: VOICE & REAL WORLD INTEL
- [x] Implementación de VITE (Entrenamiento fonético).
- [x] Primeros escaneos OSINT (RWIE).
- [x] Dashboard funcional (TMCI).

## 4. REGLAS DE DISEÑO MVP
- **Sin Nube**: Todo corre localmente para máxima velocidad de desarrollo.
- **Sin IA Compleja**: Usar reglas basadas en Regex y diccionarios para el análisis inicial.
- **80% Rule**: Monitorear siempre el hardware de la PC host.
