# REVIEW_STANDARD.md

Version: 1.0

Status: REQUIRED

Owner: CTO

---

# Objetivo

Definir el proceso oficial de revisión técnica antes de integrar cualquier Sprint.

Ningún Sprint puede integrarse sin superar esta revisión.

---

# Responsabilidad

El CTO es responsable de revisar la entrega completa antes de autorizar la integración.

La revisión tiene prioridad sobre la velocidad de desarrollo.

---

# Checklist

## Arquitectura

□ No rompe la arquitectura existente.

□ Respeta Clean Architecture.

□ Respeta SOLID.

□ Respeta MVVM.

□ No introduce acoplamiento innecesario.

---

## Packages

□ Todos los packages son correctos.

□ No existen packages duplicados.

□ No existen packages incorrectos.

---

## Imports

□ Todos los imports existen.

□ No existen imports rotos.

□ No existen imports innecesarios.

---

## Dependencias

□ Todas las dependencias fueron entregadas.

□ No faltan clases.

□ No faltan interfaces.

□ No faltan modelos.

□ No faltan Managers.

□ No faltan Repositories.

---

## Archivos

□ Todos los archivos nuevos fueron incluidos.

□ Todos los archivos modificados fueron incluidos.

□ No existen archivos faltantes.

---

## Android

□ AppContainer actualizado.

□ AndroidManifest actualizado.

□ Permisos correctos.

□ Services registrados.

□ Receivers registrados.

---

## Calidad

□ Sin TODO.

□ Sin pseudocódigo.

□ Sin código muerto.

□ Sin duplicación.

□ Sin responsabilidades repetidas.

---

## Compatibilidad

□ No rompe Bluetooth.

□ No rompe Voice.

□ No rompe Music.

□ No rompe THAMIS.

□ No rompe LogCore.

---

## Integración

□ Puede copiarse directamente.

□ No requiere reconstrucciones manuales.

□ La entrega es autocontenida.

---

# Resultado

Si cualquiera de los puntos falla:

SPRINT RECHAZADO.

Debe solicitarse una nueva entrega al Builder.

No se integra.

---

# Aprobación

Solo cuando toda la checklist sea positiva:

CTO

↓

Aprobado

↓

Integración

↓

Compilación

↓

Tests

↓

Commit

---

# Lecciones Aprendidas

Nunca integrar código sin revisión.

Nunca asumir que una entrega está completa.

Nunca corregir manualmente una entrega incompleta.

Es más rápido solicitar una nueva entrega correcta que reconstruir dependencias faltantes.

La calidad de la integración tiene prioridad sobre la velocidad.