# RULE-002 — Beta Driven Development

## Estado

Vigente

---

# Objetivo

Construir únicamente aquello que sea necesario para validar LogPose durante la Beta.

Todo lo demás se documenta para el futuro.

---

# Principio

La Beta es el objetivo del desarrollo actual.

Cada nueva clase, interfaz o componente debe justificar su existencia respondiendo una única pregunta.

> ¿Este componente será utilizado durante la Beta?

---

# Respuestas

## Sí

Se implementa.

## No

No se implementa.

Debe documentarse en:

docs/knowledge/

como:

- Idea
- Future
- Research
- ADR
- Rejected

según corresponda.

---

# Alcance de la Beta

La Beta debe validar:

- Bluetooth
- Música
- Audio Focus
- Navegación
- Notificaciones
- Llamadas
- Rendimiento
- Consumo de batería
- Compatibilidad
- Estabilidad
- LogProbe

---

# Objetivo

Evitar arquitectura innecesaria.

Evitar funcionalidades prematuras.

Evitar código sin uso.

Mantener LogCore pequeño y fácil de mantener.

---

# Pregunta obligatoria

Antes de crear cualquier componente:

> ¿Será utilizado durante la Beta?

Si la respuesta es no, no forma parte del código.

Debe quedar documentado para futuras versiones.

---

# Resultado esperado

Un MVP sólido.

Una Beta estable.

Un crecimiento controlado del proyecto.