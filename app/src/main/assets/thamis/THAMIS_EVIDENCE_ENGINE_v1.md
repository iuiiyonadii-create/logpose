# THAMIS Evidence Engine v1.0

El motor de evidencias es el encargado de proveer sustento matemático a las dudas del cerebro. Transforma hechos (contexto, fonética, gramática) en impactos de confianza.

## 1. ¿Qué es una evidencia?
Es una unidad de justificación. No es una regla rígida de ejecución, sino un "voto" a favor o en contra de una posibilidad.
Cada evidencia tiene:
- **Impacto:** Un valor (ej: +0.3) que se suma a la confianza base.
- **TTL (Time To Live):** Cuánto tiempo esta justificación es válida.

## 2. Cálculo de Confianza
Se utiliza el `ConfidenceCalculator`. 
- Empieza con un puntaje base (provisto por el detector inicial).
- Se suman todos los impactos de las evidencias encontradas.
- El resultado se limita estrictamente entre **0.0** y **1.0**.

## 3. Tipos de Evaluadores
- **Grammar:** Busca verbos de acción.
- **Phonetic:** Compara sonidos contra el diccionario ALF-R.
- **Context:** Mira el mundo exterior (ej: si Spotify está sonando, la probabilidad de música sube).
- **Risk:** Penaliza la confianza si la situación es peligrosa (ej: alta velocidad).

## 4. Auditoría
Cada decisión de THAMIS guarda el `CognitiveTrace`, que incluye la lista de todas las evidencias evaluadas. Si THAMIS comete un error, podemos ver exactamente qué evaluador sumó puntos de más.
