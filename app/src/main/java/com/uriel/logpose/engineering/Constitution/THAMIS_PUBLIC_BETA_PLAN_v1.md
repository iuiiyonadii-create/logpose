# THAMIS Public Beta Plan v1.0

## 1. Visión
La beta pública marca la transición de LogPose de un experimento de laboratorio a un asistente útil para la comunidad de motociclistas. El objetivo es recopilar datos a gran escala sobre la precisión de la voz y la seguridad de la planificación en diversas ciudades.

## 2. Proceso de Distribución
- **Canal Beta**: Distribución escalonada para grupos de 50 usuarios.
- **Control de Versiones**: Cada beta tiene un `BetaVersion` inmutable con un changelog claro.
- **Feedback Loop**: Integración de la `FeedbackPlatform` para reportes directos de bugs y mejoras de UX.

## 3. Criterio de Estabilidad
Para pasar de `PUBLIC_BETA` a `STABLE`, el sistema debe mantener un `RetentionRate` superior al 60% y un `CrashFreeRate` del 99.9%.

## 4. Soporte y Actualización
El `UpdateManager` gestionará la transición entre betas, garantizando que el `WorldModel` y la `Memory` del usuario se mantengan consistentes durante la migración de datos.
