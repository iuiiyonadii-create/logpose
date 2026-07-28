# THAMIS Security & Privacy Audit Architecture v1.0

## 1. Visión General
El motor de auditoría de seguridad es la capa encargada de garantizar la transparencia absoluta sobre el uso de recursos y la protección de la privacidad del usuario en THAMIS. A diferencia de un sistema de seguridad tradicional, su enfoque es la **auditabilidad**: ser capaz de explicar qué se usó, por qué y bajo qué regla.

## 2. Componentes Clave
- **PermissionAudit**: Registra cada acceso a recursos sensibles (Micrófono, BT, Ubicación) vinculándolos a un motivo legítimo.
- **SensorAccessMonitor**: Vigila el tiempo de uso de sensores para detectar anomalías (ej. micrófono activo por tiempo excesivo).
- **NetworkActivityMonitor**: Supervisa conexiones externas, asegurando que solo dominios autorizados sean contactados.
- **IntegrityChecker**: Valida la consistencia de los componentes internos del sistema.
- **SecurityTransparencyManager**: Traduce los eventos técnicos en explicaciones claras para el usuario final.

## 3. Principios de Privacidad
THAMIS opera bajo el principio de **"Mínimo Contenido, Máxima Traza"**. No se registra el audio capturado ni el contenido de los mensajes, sino el metadato del evento (módulo responsable, duración, motivo).

## 4. Garantía de Independencia
100% Kotlin puro. El motor no depende de las APIs de auditoría de Android, permitiendo que la lógica de seguridad sea inmutable y resistente a manipulaciones del sistema operativo.
