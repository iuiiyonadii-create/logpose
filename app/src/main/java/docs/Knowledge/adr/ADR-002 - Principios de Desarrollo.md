# ADR-002 - Principios de Desarrollo

**Estado:** Aprobado

**Versión:** 1.0

**Fecha:** 13/07/2026

**Proyecto:** LogPose

---

# Contexto

Durante el diseño inicial de LogPose se identificó la necesidad de establecer un conjunto de principios técnicos y de desarrollo que sirvan como guía para todas las decisiones futuras del proyecto.

Estos principios buscan garantizar un código mantenible, una arquitectura escalable y una experiencia consistente para el usuario.

---

# Problema

Sin reglas claras, el proyecto corre el riesgo de crecer de forma desordenada, aumentar su complejidad y dificultar el mantenimiento a largo plazo.

---

# Decisión

A partir de este momento, todo el desarrollo de LogPose deberá respetar los siguientes principios.

## 1. Una clase = una responsabilidad

Cada clase debe resolver un único problema.

No se mezclarán responsabilidades dentro de una misma clase.

---

## 2. Arquitectura modular

Cada módulo debe ser independiente y fácilmente reemplazable.

---

## 3. Investigar antes de programar

Ninguna funcionalidad importante será implementada sin una investigación previa.

---

## 4. Calidad antes que velocidad

Se priorizará la estabilidad por encima de la velocidad de desarrollo.

La Beta deberá ser pequeña pero sólida.

---

## 5. No reinventar Android

Siempre que Android proporcione una solución oficial y estable, será utilizada antes de desarrollar una implementación propia.

---

## 6. Configuración simple

Toda configuración importante deberá realizarse una sola vez.

La aplicación deberá recordar las preferencias del usuario.

---

## 7. Defensive Programming

El código deberá contemplar errores, permisos denegados, dispositivos incompatibles y comportamientos inesperados sin provocar fallos de la aplicación.

---

## 8. Código legible

El código debe ser fácil de entender antes que ingenioso.

Se priorizará la claridad sobre soluciones excesivamente complejas.

---

## 9. Escalabilidad

Toda nueva funcionalidad deberá integrarse sin afectar negativamente la arquitectura existente.

---

## 10. Documentación

Toda decisión importante deberá quedar documentada mediante un ADR o documentación oficial del proyecto.

---

# Justificación

Estos principios permitirán mantener un desarrollo consistente, reducir errores, facilitar el mantenimiento y asegurar que LogPose pueda evolucionar sin perder calidad.

---

# Consecuencias

A partir de esta decisión:

- Todas las nuevas funcionalidades deberán respetar estos principios.
- Cualquier excepción deberá estar debidamente justificada.
- Los ADR futuros deberán alinearse con estas reglas.

---

# Impacto en el proyecto

Este ADR afecta a todo el proyecto y sirve como base para la arquitectura, el desarrollo y la documentación de LogPose.

---

# Estado

✅ Decisión aprobada.