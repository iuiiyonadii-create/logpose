# THAMIS Action Scheduler v1.0

## 1. El Ciclo 'Tick'
El scheduler opera en ciclos rápidos, evaluando el primer elemento de la `PlanningQueue`.

## 2. Lógica de Decisión
- **EXECUTE_NOW**: El contexto es seguro y la prioridad es suficiente.
- **WAIT**: El contexto está ocupado (ej. llamada activa) pero el plan es válido.
- **CANCEL**: El plan es obsoleto o riesgoso dado el nuevo estado del mundo.

## 3. Manejo de Prioridades
El Scheduler permite la interrupción de planes de baja prioridad por planes `CRITICAL` (Emergencias), activando el `RollbackEngine` para el plan interrumpido si fuera necesario.
