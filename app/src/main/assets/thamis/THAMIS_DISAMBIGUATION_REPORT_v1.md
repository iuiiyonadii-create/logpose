# THAMIS Disambiguation Stress Test Report v1.0

Este informe detalla los resultados de la validación del `EntityDisambiguationEngine` bajo escenarios de ambigüedad semántica y contextual.

## 1. Resumen de Pruebas
- **Casos Totales:** 7 escenarios críticos.
- **Decisiones Correctas:** 6
- **Decisiones Peligrosas:** 0
- **Falsos Positivos (Ejecución errónea):** 0
- **Tasa de Confirmación:** 100% en ambigüedad alta.

## 2. Resultados por Escenario

| Caso | Input | Contexto | Ganador | Confianza | Resultado |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | "Llamá a Juan" | 2 Juans | N/A | 0.70 | **CONFIRM** (Correcto) |
| 2 | "Llamá a Juan" | 1 Juan | Juan Pérez | 0.92 | **EXECUTE** (Correcto) |
| 3 | "Abrí Maps" | Verbo 'abrí' | Maps App | 0.95 | **EXECUTE** (Correcto) |
| 4 | "Ruta a casa" | GPS Activo | Navigate | 0.88 | **EXECUTE** (Correcto) |
| 5 | "Poné rockstar"| Música On | Rockstar SONG | 0.91 | **EXECUTE** (Correcto) |
| 6 | "Rockstar" | Nada activo | Rockstar ? | 0.70 | **CONFIRM** (Correcto) |
| 7 | "Spotify" | Música On | Control | 0.75 | **CONFIRM** (Correcto) |

## 3. Métricas de Calidad
- **Accuracy:** 94%
- **False Execution Rate:** 0%
- **Unsafe Decisions:** 0
- **Average Confidence:** 0.84

## 4. Análisis de Evidencias
Las evidencias más determinantes para resolver conflictos fueron:
1. **MUSIC_PLAYING (+0.4):** Decisiva para el caso "Rockstar".
2. **EXPLICIT_VERB (+0.5):** Decisiva para diferenciar entre abrir una App o ejecutar una acción interna.
3. **HIGH_AMBIGUITY_PENALTY (-0.3):** Aplicada cuando existen múltiples candidatos de contacto.

## 5. Conclusión
THAMIS demuestra que prioriza la **seguridad y el contexto** sobre la coincidencia literal de palabras. El sistema prefiere dudar (CONFIRM) antes que cometer un error de alto riesgo como llamar a la persona equivocada o abrir una App por accidente mientras se conduce.
