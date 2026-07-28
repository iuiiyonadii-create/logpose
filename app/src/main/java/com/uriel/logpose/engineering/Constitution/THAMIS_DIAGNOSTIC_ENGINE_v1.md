# THAMIS Diagnostic Engine v1.0

## 1. El Proceso de Diagnóstico
El motor de diagnóstico realiza un ciclo completo de evaluación:
1. Recolecta telemetría del `TelemetryCollector`.
2. Calcula scores de salud por dominio.
3. Cruza datos con el `AnomalyDetector`.
4. Genera un `DiagnosticReport` con recomendaciones de recuperación.

## 2. Detección de Anomalías
Categorías de fallos detectados:
- **Latencia Elevada**: Cuellos de botella en el procesamiento.
- **Inconsistencia de Contexto**: Datos contradictorios en el `WorldModel`.
- **Deterioro de Confianza**: Caídas sistemáticas en los puntajes de decisión.

## 3. Asesoría de Recuperación
El sistema no ejecuta recuperaciones por sí mismo (Shadow Mode), pero entrega un listado de acciones sugeridas al orquestador principal.
