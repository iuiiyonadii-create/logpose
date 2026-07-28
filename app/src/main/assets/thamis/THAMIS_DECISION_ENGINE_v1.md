# THAMIS Decision Engine v1.0

El motor de decisiones es el "Juez" de THAMIS. Su responsabilidad es transformar el razonamiento probabilístico en un veredicto de voluntad.

## 1. Diferencia entre Pensamiento y Acción
THAMIS no ejecuta comandos. THAMIS resuelve qué es lo que el usuario quiere con un grado de certeza determinado.
- **Pensamiento:** "Hay un 90% de probabilidad de que quiera escuchar música".
- **Decisión:** "Dado el bajo riesgo, decido que debemos proceder (EXECUTE)".
- **Acción:** (Fuera de THAMIS) Traducir el veredicto en una señal para Spotify.

## 2. Cómo decide THAMIS
Se utiliza la `DecisionPolicy`, que cruza la **Confianza** (Confidence) con el **Riesgo** (Risk).
- **Riesgo Bajo (Música):** Umbral de ejecución bajo (~0.65).
- **Riesgo Medio (Navegación):** Umbral moderado (~0.85).
- **Riesgo Alto (Llamadas):** Umbral estricto (~0.95).

## 3. Prevención de Errores Peligrosos
Si el `WorldState` indica condiciones extremas (ej: velocidad > 120km/h), el motor de decisiones puede forzar un `REJECT` incluso si la confianza es alta, priorizando siempre la seguridad física del conductor sobre la conveniencia del comando.
