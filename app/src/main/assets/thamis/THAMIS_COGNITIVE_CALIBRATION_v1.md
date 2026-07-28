# THAMIS Cognitive Calibration v1.0

La calibración cognitiva es la capa de auto-auditoría de THAMIS. Su propósito es responder con datos objetivos si el cerebro está listo para gobernar las acciones de la moto.

## 1. Aprendizaje vs. Calibración
- **Aprendizaje:** El acto de registrar una experiencia (Match/Divergence).
- **Calibración:** El acto de analizar miles de experiencias para emitir un juicio de calidad técnica.

## 2. Cómo analiza THAMIS sus errores
El `CalibrationEngine` no corrige el código, sino que genera `CalibrationRecommendations`. 
- Si detecta que la confianza es alta pero el resultado es `DIVERGENCE`, sugiere **subir los umbrales** (Thresholds).
- Si detecta errores en llamadas, marca una alerta de **Riesgo Crítico**.

## 3. Uso de Reportes
Los reportes se guardan en `CalibrationMemory`. Esto permite comparar versiones:
- **v3.0.0:** 85% Accuracy.
- **v3.1.0:** 92% Accuracy.
Solo activaremos la ejecución real de THAMIS cuando un reporte de calibración demuestre una precisión superior al 95% de forma consistente.
