# THAMIS Memory Model v1.0

## 1. Gestión de Snapshots
El sistema mantiene un historial circular de estados. El `MemoryOptimizer` vigila que este historial no crezca indefinidamente, forzando la rotación de snapshots antiguos.

## 2. Detección de Fugas
El modelo registra la tasa de crecimiento (`growthRate`). Un crecimiento sostenido durante más de 10 ciclos sin liberación dispara un plan de optimización de prioridad media.

## 3. Umbrales Críticos
- **Normal**: < 5MB de uso en el núcleo cognitivo.
- **Warning**: > 10MB. Se activan limpiezas de caché automática.
- **Critical**: > 20MB. Se suspenden procesos de telemetría no esenciales.
