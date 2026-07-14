# RULE-004 — Single Responsibility

## Estado

Vigente

---

# Objetivo

Cada componente debe tener una única responsabilidad.

---

# Regla

Cada clase debe responder una única pregunta:

¿Por qué existe?

Si la respuesta contiene más de una responsabilidad,
la clase debe dividirse.

---

# Ejemplos

MusicService

Existe para aplicar reglas de negocio relacionadas con música.

MusicProvider

Existe para comunicarse con Android.

ActionEngine

Existe para coordinar la ejecución de Actions.