# THAMIS JOURNEY - INFORME DE VALIDACIÓN v1.0

## 1. Métricas de Precisión
- **Accuracy**: Medición de la coherencia entre el estado reportado y los sensores físicos.
- **False Positives**: Sesiones iniciadas sin movimiento real (vibración, etc).
- **False Starts**: Transiciones a MOVING que duran menos de 10 segundos.

## 2. Analizadores de Consistencia
El `JourneyConsistencyAnalyzer` verifica:
- Si existen saltos de velocidad imposibles.
- Si la distancia recorrida coincide con la integración de la velocidad en el tiempo.
- Si el viaje terminó en un punto geográfico válido (moto estacionada).

## 3. Umbrales de Confianza
- **Umbral de Acción**: 0.70 (Requiere múltiples evidencias sólidas).
- **Umbral de Shadow Mode**: 0.30 (Registra el intento pero no altera el estado global).
