# THAMIS Rollback Engine v1.0

## 1. El Plan de Reversión
Cada `ExecutionPlan` puede incluir un `RollbackPlan`. Este se dispara automáticamente si:
- Un paso crítico falla.
- El plan es cancelado por el Scheduler.
- Se agota el tiempo (Timeout).

## 2. Objetivos del Rollback
- Liberar el canal de audio.
- Limpiar el contexto de diálogo.
- Restaurar el volumen de la música si fue atenuado (ducking).
- Garantizar que el sistema vuelva a estado `IDLE`.
