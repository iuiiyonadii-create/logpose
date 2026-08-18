# THAMIS Language Model v1.0

## 1. Comprensión de Contexto (NLU)
THAMIS utiliza un motor de lenguaje natural determinístico. No adivina palabras, sino que utiliza el estado del mundo para rellenar vacíos en la conversación.

## 2. Manejo de Comandos Cortos
| Entrada | Contexto | Intención Resultante |
| :--- | :--- | :--- |
| "Subí" | Música activa | `INCREASE_VOLUME` |
| "A casa" | Diálogo: "¿A dónde?" | `NAV_TO_HOME` |
| "Sí" | Diálogo: "¿Confirmar?" | `EXECUTE_PENDING` |

## 3. Estructura de Salida
El resultado siempre es un `VoiceIntent` con un mapa de parámetros y un score de confianza calculado por el `ConfidenceEvaluator`.
