# THAMIS Mismatch Analysis Report v1.0

Este informe analiza las discrepancias entre la voz real del usuario en el casco y la interpretación del motor Vosk, capturadas durante las pruebas de la Fase 8 y 9.

## 1. THAMIS VOICE ERROR REPORT v1.0

**Cantidad total de eventos analizados:** 42 interacciones reales.

### Top Palabras Problemáticas
1. **Palabra:** Duki
   - **Veces:** 12
   - **Vosk interpretó:** "duque", "doce", "donde", "duqui"
   - **Corrección común:** "duki"
   - **Confianza Media:** 0.88

2. **Palabra:** Rockstar
   - **Veces:** 8
   - **Vosk interpretó:** "rock", "abrir rock", "roksar"
   - **Corrección común:** "rockstar"
   - **Confianza Media:** 0.82

3. **Palabra:** Wos
   - **Veces:** 5
   - **Vosk interpretó:** "vos", "voz"
   - **Corrección común:** "wos"
   - **Confianza Media:** 0.95

## 2. Clasificación de Errores

### Errores que THAMIS ya resuelve (Auto-Corrected)
- **Duki / Duque:** Resuelto mediante `MusicVocabulary` y `PhoneticEngine`.
- **Wos / Voz:** Resuelto por cercanía fonética ALF-R.
- **Bizarrap / Biza:** Resuelto por diccionario de alias.

### Errores Peligrosos (Requieren Atención)
- **Llamadas:** "Llamar a Juan" vs "Llamar a Juana". Vosk a veces pierde la última vocal por el ruido del escape.
- **Riesgo:** Alto (Llamada errónea).
- **Recomendación:** Activar `CONFIRM` obligatorio para el intent `CALL_CONTACT` si la confianza es < 0.95.

### Errores de Hardware / Ambiente
- **Viento (> 60km/h):** Se detecta truncamiento de palabras (ej: "Siguiente" -> "Siguie").
- **Solución:** El `ContextEvidenceEvaluator` ya penaliza estas capturas, forzando al sistema a ignorar ruidos incoherentes.

## 3. Recomendaciones Técnicas
1. **Actualizar Diccionario:** Consolidar "duque" y "doce" como alias permanentes de "duki".
2. **Contexto Rockstar:** Reforzar que si Spotify está activo, "rock" debe puntuar un +0.4 extra hacia `PLAY_MUSIC` para evitar que el sistema intente abrir una app inexistente.
3. **Filtro de Apertura:** Bloquear `OPEN_APP` si la entidad encontrada no coincide con una aplicación instalada en el `WorldState`.

## 4. Próximos Pasos
- Entrenar THAMIS con voces femeninas (actualmente el dataset es 100% masculino).
- Realizar pruebas en autopista a 100km/h para medir el límite de ruptura del motor fonético.
