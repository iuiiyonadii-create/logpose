# Proyecto LogPose: Misión de Saneamiento "Tierra Arrasada" v13.0

Este plan tiene como objetivo eliminar la redundancia masiva, unificar contratos y optimizar el rendimiento térmico y de memoria del Xiaomi Redmi 15C, eliminando el 40% de código muerto detectado en la auditoría.

## Proposed Changes

### [Componente: Purga de Código Muerto y Fantasmas]

Eliminación física de paquetes experimentales y legados que generan colisiones de lógica.

#### [DELETE] Paquete com.uriel.logpose.thamis_ai
- Borrado completo de la carpeta `app/src/main/java/com/uriel/logpose/thamis_ai`.

#### [DELETE] Redundancias en Core Bluetooth
- Borrado de `app/src/main/java/com/uriel/logpose/core/bluetooth`. (La versión activa es `features/bluetooth`).

#### [DELETE] Redundancias en Core NLP
- Borrado de `app/src/main/java/com/uriel/logpose/core/nlp`. (La versión activa es `thamis/learning`).

### [Componente: Unificación de Contratos]

#### [DELETE] com.uriel.logpose.domain.models.LogPoseCommand
- Eliminación del archivo deprecado para forzar el uso de `:core:contracts`.

#### [MODIFY] Actualización de Referencias
- Migración de cualquier clase que aún use el enum `domain.models.LogPoseCommand` al contrato unificado.

### [Componente: Optimización y Fixes de Xiaomi]

#### [MODIFY] [BluetoothViewModel.kt](file:///C:/projects/LogPose4/app/src/main/java/com/uriel/logpose/ui/viewmodel/BluetoothViewModel.kt)
- Refactor del `batteryReceiver` para usar el ciclo de vida del ViewModel de forma segura y evitar fugas de memoria.

#### [MODIFY] [VoskVoiceEngine.kt](file:///C:/projects/LogPose4/app/src/main/java/com/uriel/logpose/features/voice/VoskVoiceEngine.kt)
- Eliminación de la variable no usada `musicController`.

#### [MODIFY] [app/build.gradle.kts](file:///C:/projects/LogPose4/app/build.gradle.kts)
- Eliminación de la declaración duplicada de `androidx.compose.bom`.

## Verification Plan

### Automated Tests
- Ejecución de `.\gradlew.bat clean assembleProductionDebug` para asegurar que el borrado no rompió el árbol de dependencias.

### Manual Verification
- Verificación en el dispositivo de que el "lo" fix sigue operativo y que el Bluetooth conecta sin saturar el sistema.
