# Matriz de Compatibilidad de Dispositivos (Estrategia LogPose)

## 1. Desafíos por Marca

### Xiaomi (MIUI / HyperOS) - **CRÍTICO**
*   **Problema:** Cierre agresivo de servicios en segundo plano y bloqueo de IPC (Error 10/5).
*   **Solución LogPose:** Uso de motor On-Device (Offline) y activación manual pura para evitar el "Silent Kill" del sistema. Requiere permiso de "Ventanas emergentes" para persistir.

### Samsung (One UI)
*   **Problema:** "Put apps to sleep" suspende el reconocimiento de voz si no hay actividad en la UI.
*   **Solución LogPose:** Servicio de Primer Plano (Foreground Service) con notificación de alta prioridad y categoría de "Media/Assistant".

### Motorola / Oppo / Realme
*   **Problema:** Gestores de batería que ignoran las excepciones de optimización.
*   **Solución LogPose:** Implementar `ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` durante el setup inicial.

### Huawei (EMUI)
*   **Problema:** Falta de Google Play Services en modelos nuevos.
*   **Solución LogPose:** Aquí es donde **Vosk** (nuestro motor independiente) es vital. LogPose debe funcionar sin depender de los servicios de Google para ser universal.

## 2. Compatibilidad por Versión de Android

| Versión | API | Estado | Requisito Especial |
| :--- | :--- | :--- | :--- |
| Android 9-10 | 28-29 | Compatible | Permisos de Bluetooth Legacy. |
| Android 11 | 30 | Compatible | Declaración de `<queries>` en Manifest. |
| Android 12-13 | 31-33 | Óptimo | Bluetooth Connect/Scan permissions + On-Device STT. |
| Android 14+ | 34+ | Óptimo | Flags de BroadcastReceiver EXPORTED/NOT_EXPORTED. |

## 3. Acciones para la Robustez
1. **Desacoplamiento de Hardware:** THAMIS no sabe qué marca de teléfono usas; él solo recibe texto. Toda la "pelea" con las marcas queda aislada en `core.compat`.
2. **Abstracción de Servicios:** El `LogPoseService` debe ser el único punto de contacto con el ciclo de vida de Android.
