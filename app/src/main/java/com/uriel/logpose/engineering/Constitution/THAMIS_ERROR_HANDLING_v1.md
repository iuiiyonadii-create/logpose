# THAMIS Error Handling & Resilience Specification v1.0

Este documento constituye la norma oficial para la detección, diagnóstico, recuperación y aprendizaje de fallos en el ecosistema LogPose4. Su objetivo es garantizar que el sistema nunca quede en un estado ambiguo y que el motociclista reciba feedback claro sin comprometer su seguridad.

---

## 1. Detección y Autodiagnóstico Interno

THAMIS no debe simplemente "fallar"; debe categorizar la causa raíz para alimentar el `LearningEngine` y ajustar los umbrales del `SurvivalFilter`.

### Categorías de Autodiagnóstico:
- **VOICE_SILENCE**: El micro se activó pero no se detectó energía de audio (VAD).
- **VOICE_CLIPPING**: Ruido extremo (viento/motor) que satura el espectro (Confianza STT < 0.2).
- **NLU_VERB_UNKNOWN**: Se reconoció el texto pero no se encontró un verbo operativo en el Glosario.
- **NLU_ENTITY_MISSING**: Verbo detectado pero falta el objeto (Ej: "Llevame a...").
- **ENTITY_AMBIGUITY**: Múltiples resultados para un contacto o destino.
- **HARDWARE_BT_LOST**: Intercomunicador desconectado físicamente.
- **HARDWARE_GPS_LOST**: Pérdida de FIX de satélite durante navegación.
- **SERVICE_APP_UNAVAILABLE**: La aplicación destino (Spotify/Maps) no responde o no está instalada.
- **PERMISSION_DENIED**: Falta de permisos de Android para ejecutar la acción.
- **NETWORK_OFFLINE**: Acción requiere red (ej: búsqueda en la nube) y no hay datos.

---

## 2. Reglas de Comportamiento y Feedback

### A. El "Oído" (No escuchó / No entendió)
1. **Fallo 1**: Thamis responde: *"No entendí ese comando."* (Estado: `IDLE`).
2. **Fallo 2 (Regla de los 2 Intentos)**: Si el siguiente comando consecutivo también falla el NLU, Thamis responde: *"Uriel, hay mucho viento o no te entiendo. Probamos en un rato."* y entra en **Deep Sleep** (Desactiva Vosk por 60 segundos o hasta evento de velocidad < 20km/h).

### B. Errores durante otras Actividades
- **En Llamada**: Los errores de NLU o notificaciones se registran en `ShadowMode` pero **no emiten voz** para no interrumpir al interlocutor. Solo se avisa ante `HARDWARE_BT_LOST`.
- **En Música**: El error emite el feedback, hace "Unduck" de la música y vuelve a `IDLE`.
- **En Navegación**: Si falla un comando de voz mientras se navega, Thamis da el error y **mantiene la guía visual/auditiva activa**. No se cancela la ruta por errores de voz.
- **En Mensajería**: Si falla el envío, Thamis dice: *"No pude mandarlo, che."* y guarda el borrador en `TemporaryMemory`.

---

## 3. Estados Posteriores al Error

LogPose debe resetear su flujo conversacional tras un fallo para evitar bucles de escucha.

| Tipo de Error | Estado Siguiente | Acción Técnica |
| :--- | :--- | :--- |
| Comprensión (NLU) | `IDLE` | Flush de acumuladores en `CommandIntegrityBuffer`. |
| Ejecución (App) | `IDLE` | Log de `ActuationResult.FAILURE`. |
| Conexión (BT) | `RETRYING` | Intento de reconexión SCO (3 veces). |
| Permiso / Red | `FINISHED` | Notificación en el HUD de Android. |
| Crítico (Crash) | `FAILED` | Auto-reinicio del `TripOrchestrator` vía `AutoRecovery`. |

---

## 4. Corrección y Aprendizaje (The "No, quise decir" Protocol)

THAMIS debe diferenciar entre cancelar una acción y corregir una interpretación errónea.

### Gatillos de Corrección:
- *"No, quise decir [Nuevo Comando]"*
- *"No era eso, [Nuevo Comando]"*
- *"Me equivoqué, [Nuevo Comando]"*

### Lógica de Resolución:
1. **Detección**: Si la frase empieza con "No" seguido de un Verbo Operativo en una ventana de < 5s tras una acción de THAMIS.
2. **Acción Anterior**: Si la acción anterior está ejecutándose (ej: abriendo Maps), se intenta detener/revertir.
3. **Nueva Acción**: El [Nuevo Comando] entra al pipeline con **Prioridad 1000** (Override).
4. **Learning**: La frase original que causó el error se marca como `MISMATCH` y se vincula al `target_intent` del nuevo comando para corregir el peso fonético en el futuro.

---

### 5. Respuestas TTS Oficiales (Maya)

| Evento | Frase Exacta (Español AR) |
| :--- | :--- |
| **Error Genérico** | "No entendí ese comando." |
| **Fallo 2 (Viento)** | "Hay mucho viento o no te entiendo. Probamos en un rato." |
| **Pérdida GPS** | "Perdí el satélite, Uriel. No puedo guiarte ahora." |
| **Fallo Spotify** | "Spotify no me responde, fijate si está abierto." |
| **Fallo Bluetooth** | "Se me desconectó el casco. Reintentando..." |
| **Contacto no hallado**| "No encontré a ese contacto en tu agenda." |
| **Permisos** | "Me falta un permiso de Android para hacer eso." |
| **Cancelación** | "Dale, cancelado." |

---

## 6. Prioridades de Seguridad (Filtro v32)
- **Regla Oro 1**: Nunca interrumpir una maniobra de emergencia con un error de música.
- **Regla Oro 2**: Si el casco se desconecta a > 80 km/h, el aviso TTS se emite por el altavoz del teléfono a volumen máximo solo si la navegación está activa.
