# ADR-004 - Estrategia de Integración con Aplicaciones Externas

**Estado:** Aprobado

**Versión:** 1.0

**Fecha:** 13/07/2026

**Proyecto:** LogPose

---

# Contexto

LogPose interactuará con múltiples aplicaciones de terceros, como reproductores de música, aplicaciones de navegación y plataformas de mensajería.

Durante el análisis inicial surgió la posibilidad de desarrollar implementaciones propias para reemplazar algunas de estas funciones.

Después de la investigación se concluyó que esta estrategia aumentaría innecesariamente la complejidad del proyecto y generaría problemas de mantenimiento y compatibilidad.

---

# Problema

Intentar reemplazar aplicaciones especializadas implica mantener funcionalidades extremadamente complejas que ya han sido resueltas por sus desarrolladores.

Además, cualquier cambio realizado por una aplicación externa podría romper la integración si se utilizan métodos no oficiales.

---

# Decisión

LogPose no reemplazará aplicaciones existentes.

Su función será integrarlas de forma segura, estable y transparente.

Siempre que exista una API oficial, un SDK oficial o una funcionalidad estándar de Android, esa será la primera opción.

Solo se implementarán soluciones alternativas cuando no exista otra posibilidad razonable.

---

# Principios

## Cada aplicación hace lo que mejor sabe hacer.

Spotify reproduce música.

Google Maps navega.

WhatsApp administra conversaciones.

LogPose coordina la experiencia del conductor.

---

## No duplicar funcionalidades.

Si una aplicación ya resuelve correctamente un problema, LogPose no desarrollará una versión propia de esa función.

---

## Priorizar APIs oficiales.

Las integraciones deberán utilizar APIs, SDKs o mecanismos documentados oficialmente siempre que sea posible.

---

## Compatibilidad antes que funciones.

Una integración estable es más importante que una integración con muchas funciones pero frágil.

---

## Independencia.

La falla de una aplicación externa nunca deberá provocar el fallo de LogPose.

Cada integración deberá aislar sus errores y permitir que el resto del sistema continúe funcionando.

---

# Justificación

Delegar responsabilidades en aplicaciones especializadas permite reducir la complejidad, mejorar la estabilidad y disminuir el mantenimiento del proyecto.

Además, facilita la adaptación a futuras versiones de Android.

---

# Consecuencias

A partir de esta decisión:

- No se desarrollará un reproductor de música propio.
- No se desarrollará un navegador propio.
- No se desarrollará un sistema de mensajería propio.
- Se priorizarán integraciones oficiales.
- Cada integración será modular e independiente.

---

# Impacto en el proyecto

Este ADR afecta a:

- Music Engine.
- Navigation Engine.
- Notification Engine.
- Voice Assistant.
- Arquitectura general.

También servirá como referencia para cualquier integración futura.

---

# Principio Fundamental

> "Controlamos la experiencia del usuario, no reemplazamos las aplicaciones que ya hacen bien su trabajo."

---

# Estado

✅ Decisión aprobada.