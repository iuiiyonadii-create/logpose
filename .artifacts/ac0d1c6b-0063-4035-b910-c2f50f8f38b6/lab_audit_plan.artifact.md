# Plan de Auditoría y Hardening de THAMIS Lab

Este plan aborda la optimización y robustez de los módulos de laboratorio y simulación para asegurar que el motor de calidad sea tan fiable como el código de producción.

## Objetivos
- **Desacoplamiento**: Introducir una capa de inyección de dependencias ligera (manual o Koin) en los módulos JVM para facilitar el testing.
- **Observabilidad**: Implementar un sistema de trazas unificado entre los módulos `:lab`.
- **Escalabilidad**: Refactorizar el `UnifiedSimulationOrchestrator` para soportar ejecución paralela de escenarios.
- **Seguridad**: Asegurar que los puertos UDP/TCP utilizados en el laboratorio sean configurables y no choquen con servicios del sistema.

## Proposed Changes

### 1. Infraestructura Core (`:core:common`)
#### [NEW] [LabTelemetry.kt](file:///C:/projects/LogPose4/core/common/src/main/kotlin/com/thamis/lab/core/common/telemetry/LabTelemetry.kt)
- Centralización de logs y métricas de ejecución de laboratorio.

### 2. Orquestación (`:lab:orchestrator`)
#### [MODIFY] [UnifiedSimulationOrchestrator.kt](file:///C:/projects/LogPose4/lab/orchestrator/src/main/kotlin/com/thamis/lab/orchestrator/UnifiedSimulationOrchestrator.kt)
- Inyección de dependencias en el constructor.
- Soporte para `CoroutineScope` externo para ejecuciones asíncronas.

### 3. Inteligencia de Laboratorio (`:lab:intelligence`)
#### [MODIFY] [AiAnalysisEngine.kt](file:///C:/projects/LogPose4/lab/intelligence/src/main/kotlin/com/thamis/lab/intelligence/core/AiAnalysisEngine.kt)
- Mejora de los algoritmos de puntuación para incluir análisis de latencia real y consumo de memoria simulado.

### 4. Interfaz de Usuario (`:ui:mission-control`)
#### [MODIFY] [MissionControlView.kt](file:///C:/projects/LogPose4/ui/mission-control/src/main/kotlin/com/thamis/ui/missioncontrol/MissionControlView.kt)
- Refactorización de la generación de HTML para usar un motor de plantillas simple o `String.build` más robusto.

## Verification Plan

### Automated Tests
- Ejecutar `./gradlew :lab:orchestrator:test`
- Ejecutar `./gradlew :lab:intelligence:test`

### Manual Verification
- Lanzar `LaunchMissionControl` y verificar que el dashboard se genera correctamente con datos simulados.
