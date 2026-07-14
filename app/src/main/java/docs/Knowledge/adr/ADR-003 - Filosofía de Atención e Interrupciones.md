# ADR-003 - Filosofía de Atención e Interrupciones

**Estado:** Aprobado

**Versión:** 1.0

**Fecha:** 13/07/2026

**Proyecto:** LogPose

---

# Contexto

Durante el diseño inicial de LogPose surgió una pregunta fundamental:

¿Cómo debe interactuar una aplicación con un motociclista que conduce mientras trabaja?

Inicialmente se pensó en desarrollar un sistema capaz de leer notificaciones, reproducir mensajes y controlar diferentes aplicaciones mediante voz.

Sin embargo, durante la investigación se concluyó que el verdadero problema no era acceder a la información, sino decidir cuándo esa información merece interrumpir al conductor.

Esta decisión redefine la filosofía completa del proyecto.

---

# Problema

Cada aplicación considera que sus notificaciones son importantes.

Mientras una persona conduce una motocicleta, esa lógica deja de ser válida.

Interrumpir innecesariamente al conductor aumenta la carga cognitiva, reduce la concentración y afecta la seguridad.

El verdadero desafío no consiste en leer más información, sino en seleccionar únicamente aquella que realmente aporta valor en ese momento.

---

# Decisión

LogPose no será un lector de notificaciones.

LogPose será un administrador inteligente de la atención del conductor.

La aplicación decidirá cuándo interrumpir, cuándo esperar, cuándo agrupar información y cuándo permanecer completamente en silencio.

Toda interrupción deberá estar justificada por su utilidad para el usuario.

---

# Principios

## La atención del conductor es limitada.

Cada interrupción consume parte de esa atención.

Por este motivo, cada mensaje leído deberá justificar la interrupción que genera.

---

## El silencio también es una decisión.

No informar una notificación irrelevante es una acción consciente del sistema.

El silencio forma parte de la experiencia de conducción.

---

## No todas las notificaciones tienen el mismo valor.

Un mensaje privado, una llamada o una indicación de navegación pueden ser importantes.

Un "Me gusta", un Reel o una sugerencia de una red social normalmente no lo son.

LogPose deberá distinguir entre ambos casos.

---

## Agrupar antes que repetir.

Cuando varias notificaciones pertenezcan a la misma conversación o evento, deberán agruparse para evitar saturar al usuario.

El objetivo es informar una sola vez con la información suficiente.

---

## El conductor mantiene el control.

LogPose podrá sugerir configuraciones inteligentes, pero será el usuario quien decida qué aplicaciones y qué tipos de eventos desea escuchar durante la conducción.

---

## La conducción tiene prioridad.

Siempre que exista un conflicto entre informar y mantener la concentración del conductor, la prioridad será preservar la atención.

---

# Justificación

El objetivo de LogPose no es transmitir toda la información disponible.

El objetivo es transmitir únicamente la información que ayuda al conductor en ese momento.

Reducir interrupciones mejora la seguridad, disminuye el estrés durante la conducción y genera una experiencia mucho más natural.

---

# Consecuencias

A partir de esta decisión:

- Las notificaciones serán clasificadas por relevancia.
- El sistema podrá agrupar múltiples eventos.
- El usuario podrá personalizar qué desea escuchar.
- Se priorizará la calidad de las interrupciones por encima de la cantidad de información transmitida.

---

# Impacto en el proyecto

Esta decisión afecta directamente a:

- Notification Engine.
- Voice Assistant.
- Configuración del Modo Conducción.
- Sistema de prioridades.
- Futuro motor de inteligencia contextual.

También influirá en cualquier módulo que interactúe con el usuario mediante voz.

---

# Principio Fundamental

> "La atención del conductor es el recurso más valioso."

---

# Estado

✅ Decisión aprobada.