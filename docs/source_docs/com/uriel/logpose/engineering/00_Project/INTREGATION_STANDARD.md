# INTEGRATION_STANDARD.md

Version: 1.0

Status: REQUIRED

Owner: CTO

---

# Objetivo

Definir el proceso oficial para integrar cualquier Sprint dentro de LogPose.

Toda integración debe preservar la estabilidad del proyecto.

---

# Flujo Oficial

Builder

↓

Entrega

↓

Code Review (CTO)

↓

Integración

↓

Compilación

↓

Correcciones

↓

Tests

↓

Aprobación

↓

Commit

---

# Builder

El Builder entrega únicamente:

- Archivos nuevos.
- Archivos modificados.

Nunca entrega:

- Proyecto completo.
- Archivos innecesarios.
- Código experimental.

---

# Code Review

Antes de integrar, el CTO revisará:

Packages.

Imports.

Referencias.

Dependencias.

Archivos nuevos.

Archivos modificados.

AppContainer.

AndroidManifest.

Arquitectura.

Compatibilidad.

Integridad de la entrega.

---

# Regla Fundamental

Si falta un archivo:

NO integrar.

Solicitar una nueva entrega.

Nunca reconstruir archivos manualmente salvo autorización del CTO.

---

# Integración

El usuario únicamente integrará archivos previamente aprobados.

Nunca integrar directamente una entrega sin revisión.

---

# Compilación

Después de integrar:

1. Sync Gradle.

2. Build → Make Project.

3. Resolver errores uno por uno.

Nunca intentar resolver múltiples errores simultáneamente.

---

# Validación

Después de compilar:

Ejecutar Tests.

Verificar regresiones.

Verificar arquitectura.

Verificar funcionamiento.

---

# Aprobación

Un Sprint se considera aprobado únicamente cuando:

✓ Integra correctamente.

✓ Compila.

✓ No rompe módulos existentes.

✓ Mantiene la arquitectura.

✓ Supera la revisión del CTO.

✓ Está listo para producción interna.

---

# Lecciones Aprendidas

Las entregas deben ser autocontenidas.

Nunca asumir que una dependencia ya existe.

Nunca omitir archivos relacionados.

Los cambios deben entregarse completos para evitar errores de integración.

La revisión del CTO es obligatoria antes de cualquier integración.