# ADR-008 - Investigación Antes del Desarrollo

**Estado:** Aprobado

**Versión:** 1.0

**Fecha:** 13/07/2026

**Proyecto:** LogPose

---

# Contexto

Durante el desarrollo inicial de LogPose se observó que muchas decisiones técnicas podían tomarse rápidamente basándose únicamente en experiencia previa o suposiciones.

Sin embargo, debido a la naturaleza del proyecto y a la gran cantidad de integraciones con Android, Bluetooth, aplicaciones de terceros e inteligencia contextual, este enfoque representaba un riesgo importante para la estabilidad y escalabilidad del sistema.

Se decidió adoptar una metodología basada en la investigación previa a cualquier implementación significativa.

---

# Problema

Programar sin comprender completamente el problema suele generar:

- Arquitecturas difíciles de mantener.
- Código que debe reescribirse.
- Funcionalidades incompletas.
- Integraciones frágiles.
- Decisiones tomadas por intuición en lugar de evidencia.

En un proyecto como LogPose esto incrementa considerablemente el riesgo de errores futuros.

---

# Decisión

Toda funcionalidad importante deberá atravesar las siguientes etapas antes de comenzar su implementación.

1. Comprender el problema.
2. Investigar el funcionamiento de la tecnología involucrada.
3. Analizar alternativas.
4. Diseñar la arquitectura.
5. Documentar las decisiones.
6. Recién entonces comenzar el desarrollo.

---

# Principios

## Comprender antes de construir.

No se desarrollará ninguna funcionalidad importante sin entender completamente el problema que intenta resolver.

---

## La investigación forma parte del desarrollo.

Investigar no se considera tiempo perdido.

Es una etapa obligatoria del proceso de ingeniería.

---

## Decidir con evidencia.

Las decisiones deberán apoyarse en documentación oficial, pruebas reales, investigación técnica y experiencia práctica.

No únicamente en intuición.

---

## Diseñar antes de programar.

La arquitectura deberá definirse antes de escribir código.

El código será una consecuencia del diseño, nunca al revés.

---

## Documentar las decisiones.

Toda decisión importante deberá quedar registrada mediante un ADR o documentación oficial.

El conocimiento del proyecto no dependerá de la memoria de sus desarrolladores.

---

## Investigar al usuario.

La investigación no se limitará a aspectos técnicos.

También deberá comprender:

- necesidades del usuario;
- contexto de uso;
- hardware habitual;
- aplicaciones utilizadas;
- problemas cotidianos.

---

# Justificación

Invertir tiempo en investigación reduce significativamente la necesidad de reescribir código y mejora la calidad de las decisiones técnicas.

Comprender primero el problema permite construir soluciones más simples, estables y sostenibles.

---

# Consecuencias

A partir de esta decisión:

- Ninguna integración importante comenzará directamente con código.
- Se crearán documentos de investigación para los componentes relevantes.
- La documentación crecerá junto con el proyecto.
- Las decisiones técnicas estarán respaldadas por evidencia.

---

# Impacto en el proyecto

Este ADR afecta a todo el proceso de desarrollo de LogPose.

Será utilizado como referencia para:

- investigaciones;
- arquitectura;
- planificación;
- diseño de módulos;
- incorporación de nuevas funcionalidades.

---

# Principio Fundamental

> "Primero comprendemos el problema. Después escribimos código."

---

# Estado

✅ Decisión aprobada.