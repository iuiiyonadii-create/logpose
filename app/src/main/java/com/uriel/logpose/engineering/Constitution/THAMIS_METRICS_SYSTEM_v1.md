# THAMIS Metrics System v1.0

## 1. KPIs Globales
- **Stability Index**: Porcentaje de operaciones completadas sin fallo (0.0 a 1.0).
- **Average Global Latency**: Promedio de respuesta de todo el sistema.
- **Cognitive Load**: Cantidad de eventos procesados simultáneamente.

## 2. Métricas de Resiliencia
- **Recovery Success Rate**: Porcentaje de planes de recuperación que sanaron el sistema.
- **Interruption Count**: Veces que un dominio de alta prioridad cortó a uno de baja.

## 3. Almacenamiento
Las métricas son volátiles (en sesión) pero se exportan a trazas auditables mediante el `PerformanceAudit` para su persistencia en futuras fases.
