# ADR-009 - Estrategia de la Beta

**Estado:** Aprobado

**Versión:** 1.0

**Fecha:** 13/07/2026

**Proyecto:** LogPose

---

# Contexto

Desde el inicio del proyecto se decidió que LogPose sería una herramienta utilizada durante la conducción de una motocicleta.

Esto implica una responsabilidad mayor que la de una aplicación convencional.

Un error puede generar distracciones, pérdida de confianza por parte del usuario o afectar negativamente su experiencia durante el trabajo.

Por este motivo, la estrategia de lanzamiento debía priorizar la estabilidad antes que la cantidad de funcionalidades.

---

# Problema

Existe la tentación de incorporar continuamente nuevas funciones antes de terminar las ya existentes.

Este enfoque genera Betas difíciles de probar, aumenta la cantidad de errores y retrasa la obtención de un producto realmente confiable.

---

# Decisión

La Beta de LogPose tendrá un alcance claramente definido.

Solo incluirá funcionalidades consideradas estables, necesarias y validadas en escenarios reales.

Toda función que no alcance el nivel de calidad esperado será pospuesta para versiones posteriores.

---

# Principios

## La Beta es un producto.

Aunque no sea una versión final, deberá ofrecer una experiencia completa y confiable.

---

## Menos funciones, mayor calidad.

Agregar funcionalidades nunca tendrá prioridad sobre mejorar las existentes.

---

## Sin errores críticos.

No se publicará una Beta con errores conocidos que afecten la experiencia principal del usuario.

---

## Validación en condiciones reales.

Toda funcionalidad deberá probarse utilizando el hardware y el contexto de uso definidos para el proyecto.

---

## Cada módulo debe estar terminado.

Los módulos incluidos en la Beta deberán encontrarse completos dentro del alcance definido para esa versión.

No existirán módulos "a medio hacer".

---

## El usuario prueba un producto, no un experimento.

Cada versión publicada deberá transmitir confianza y estabilidad.

---

# Justificación

La primera impresión del usuario determinará la percepción del proyecto.

Una Beta estable genera confianza, facilita la detección de problemas reales y permite evolucionar el producto sobre una base sólida.

---

# Consecuencias

A partir de esta decisión:

- Las nuevas funciones podrán retrasarse si afectan la estabilidad.
- La planificación priorizará calidad antes que cantidad.
- Cada módulo tendrá criterios claros para considerarse finalizado.
- La Beta crecerá de manera incremental.

---

# Impacto en el proyecto

Este ADR afecta a:

- Roadmap.
- Planificación de versiones.
- Criterios de finalización.
- Estrategia de pruebas.
- Gestión de calidad.

---

# Principio Fundamental

> "Una Beta pequeña y estable vale más que una Beta grande e inestable."

---

# Estado

✅ Decisión aprobada.