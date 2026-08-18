# THAMIS Performance Profiler v1.0

## 1. Métricas Soportadas
- **Módulos**: Navigation, Multimedia, Planning, Communication, Dialog, WorldModel.
- **Latencia de Pipeline**: Tiempo total desde la captura de voz hasta la ejecución.
- **Frecuencia de Procesamiento**: Hertz de actualización del WorldModel.
- **Tasa de Errores**: Relación entre operaciones fallidas y exitosas.

## 2. Detección de Degradación
El sistema marca un módulo como "degradado" si:
- La latencia promedio supera los 300ms.
- La latencia máxima supera los 1000ms.
- El error rate es superior al 5%.

## 3. Auditoría Forense
A través de `PerformanceTrace`, cada micro-segundo de retraso es registrado junto con su contexto, permitiendo identificar si la lentitud es causada por ruido ambiente, falta de GPS o saturación de mensajes.
