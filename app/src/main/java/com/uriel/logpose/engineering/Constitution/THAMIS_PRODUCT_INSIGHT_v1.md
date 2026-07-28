# THAMIS Product Insight Model v1.0

## 1. Definición de Insight
Un `ProductInsight` es el resultado de un análisis de frecuencia y severidad sobre el feedback de usuarios. Representa una oportunidad detectada para mejorar la resiliencia o utilidad del sistema.

## 2. Categorías de Análisis
- **VOICE_CONFUSION**: Alta tasa de repetición de comandos.
- **UX_FRICTION**: El usuario cancela acciones iniciadas por el asistente.
- **SAFETY_ALERT**: El usuario reporta distracciones en momentos de alta carga cognitiva.
- **FEATURE_REQUEST**: Patrones de palabras clave detectados en feedback informal.

## 3. Ponderación de Impacto
Los insights que afectan a la **Seguridad (SAFETY)** o a la **Estabilidad (BUG)** reciben prioridad inmediata (Score 100) en el pipeline de mejora.
