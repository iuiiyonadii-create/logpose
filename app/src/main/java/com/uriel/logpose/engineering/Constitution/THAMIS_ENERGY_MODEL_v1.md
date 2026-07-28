# THAMIS Energy Model v1.0

## 1. Niveles de Actividad
El consumo energético se correlaciona directamente con la frecuencia de procesamiento:
- **IDLE**: 5Hz (Mínimo consumo).
- **ACTIVE**: 10Hz-15Hz (Procesamiento normal).
- **CRITICAL**: 20Hz (Máxima respuesta, mayor impacto térmico).

## 2. Estrategias de Ahorro
Cuando `EnergyAnalyzer` detecta consumo elevado o batería baja (<15%), propone:
- Reducir la frecuencia de snapshots del mundo.
- Posponer análisis estadísticos de telemetría.
- Simplificar el pipeline de planificación.
