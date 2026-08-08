# [Communication Autonomy] Misión #017 - WhatsApp & Call Control

Este plan transforma a THAMIS en un asistente de comunicación 100% manos libres mediante la automatización de clics en WhatsApp y el control directo de llamadas del sistema.

## User Review Required

> [!WARNING]
> El uso de un **Accessibility Service** requiere que el usuario lo habilite manualmente en los ajustes de Android por razones de seguridad del sistema operativo. Sin este paso, el envío automático de WhatsApp no funcionará.

## Proposed Changes

### [App: Accessibility]
Summary: Implement service to click the WhatsApp "Send" button automatically.

#### [NEW] [LogPoseAccessibilityService.kt](file:///C:/projects/LogPose4/app/src/main/java/com/uriel/logpose/core/services/LogPoseAccessibilityService.kt)
Detects when the WhatsApp chat window is opened and clicks the send button if a pending message exists.

#### [NEW] [accessibility_service_config.xml](file:///C:/projects/LogPose4/app/src/main/res/xml/accessibility_service_config.xml)
Configuration for the accessibility service.

---

### [App: Telephony]
Summary: Full call control (Answer/Reject) via voice.

#### [NEW] [LogPoseInCallService.kt](file:///C:/projects/LogPose4/app/src/main/java/com/uriel/logpose/core/services/LogPoseInCallService.kt)
Implementation of `InCallService` to capture and control active calls.

---

### [App: Manifest & Integration]
Summary: Register services and wire up the dispatcher.

#### [MODIFY] [AndroidManifest.xml](file:///C:/projects/LogPose4/app/src/main/AndroidManifest.xml)
Register the Accessibility and InCall services.

#### [MODIFY] [CommandDispatcher.kt](file:///C:/projects/LogPose4/app/src/main/java/com/uriel/logpose/core/engine/CommandDispatcher.kt)
Update the `ConfirmAction` and `AcceptCall` logic to use the new automated services.

## Verification Plan

### Automated Tests
- N/A (Servicios de sistema requieren entorno real).

### Manual Verification
1. Compilar y desplegar en el móvil.
2. Habilitar "LogPose Accessibility" en Ajustes -> Accesibilidad.
3. Decir: *"Che, mandale un wasap a [Contacto] que diga Hola"*.
4. Verificar que WhatsApp se abre y el mensaje se envía solo sin tocar la pantalla.
5. Simular llamada entrante y decir *"Atendé"*. Verificar que la llamada se conecta.
