# ADR-006 - Arquitectura Modular

**Estado:** Aprobado

**Versión:** 1.0

**Fecha:** 13/07/2026

**Proyecto:** LogPose

---

# Contexto

Desde las primeras etapas del desarrollo se observó que LogPose debía integrar múltiples sistemas independientes, como Bluetooth, navegación, música, notificaciones, reconocimiento de voz y futuras funcionalidades.

Si estos componentes compartieran demasiadas responsabilidades, el crecimiento del proyecto provocaría una arquitectura difícil de mantener y propensa a errores.

Era necesario establecer una estrategia arquitectónica que permitiera desarrollar cada módulo de forma independiente.

---

# Problema

Cuando diferentes funcionalidades comparten código o responsabilidades, cualquier modificación puede producir errores inesperados en otras partes del sistema.

Además, resulta más difícil realizar pruebas, corregir errores e incorporar nuevos desarrolladores al proyecto.

---

# Decisión

LogPose será desarrollado mediante una arquitectura modular.

Cada módulo será responsable de un único dominio funcional y podrá evolucionar de forma independiente sin afectar al resto del sistema.

La comunicación entre módulos deberá realizarse únicamente mediante interfaces claramente definidas.

---

# Principios

## Un módulo, un propósito.

Cada módulo deberá resolver un único problema del sistema.

Ejemplos:

- Bluetooth
- Música
- Navegación
- Notificaciones
- Voz

Nunca deberán mezclarse responsabilidades.

---

## Bajo acoplamiento.

Los módulos deberán depender lo menos posible entre sí.

Una modificación en un módulo no deberá requerir cambios en otros módulos salvo cuando exista una justificación arquitectónica.

---

## Alta cohesión.

Todo el código relacionado con una funcionalidad deberá permanecer dentro del mismo módulo.

---

## Independencia.

Cada módulo deberá poder desarrollarse, probarse y mantenerse de forma aislada.

---

## Interfaces claras.

La comunicación entre módulos deberá realizarse mediante contratos bien definidos.

Ningún módulo deberá acceder directamente a la implementación interna de otro.

---

## Escalabilidad.

Agregar una nueva funcionalidad deberá implicar crear un nuevo módulo o extender uno existente sin romper la arquitectura general.

---

## Sustitución.

Siempre que sea posible, un módulo deberá poder reemplazarse por otra implementación sin afectar al resto del sistema.

---

# Justificación

Una arquitectura modular facilita el mantenimiento, mejora la legibilidad del código, reduce errores y permite que LogPose continúe creciendo durante años sin necesidad de reorganizar completamente el proyecto.

---

# Consecuencias

A partir de esta decisión:

- Cada nueva funcionalidad deberá pertenecer a un módulo claramente identificado.
- Se evitarán clases con múltiples responsabilidades.
- Se favorecerá la reutilización de componentes.
- Las pruebas podrán realizarse módulo por módulo.

---

# Impacto en el proyecto

Este ADR afecta a todo el código fuente de LogPose.

Será la referencia principal para cualquier decisión relacionada con arquitectura, organización de paquetes y distribución de responsabilidades.

---

# Principio Fundamental

> "Cada módulo debe poder crecer sin romper a los demás."

---

# Estado

✅ Decisión aprobada.