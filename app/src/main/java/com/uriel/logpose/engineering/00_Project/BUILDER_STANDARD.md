# BUILDER_STANDARD.md

Version: 1.0

Status: REQUIRED

Owner: CTO

---

# Objetivo

Definir el estándar obligatorio que debe seguir cualquier Builder (Claude u otra IA) durante el desarrollo de LogPose.

Todo Sprint debe ser entregado listo para integrar.

El Builder no es responsable de la arquitectura del proyecto.

El Builder implementa la arquitectura definida por el CTO.

---

# Responsabilidades

El Builder debe:

- Implementar únicamente el Sprint asignado.
- Respetar la arquitectura existente.
- Reutilizar componentes antes de crear nuevos.
- Mantener compatibilidad.
- Escribir código limpio.
- Mantener SOLID.
- Mantener Clean Architecture.

---

# Prohibido

Nunca:

- Cambiar la arquitectura.
- Reemplazar componentes funcionales.
- Duplicar clases.
- Duplicar responsabilidades.
- Crear implementaciones paralelas.
- Romper compatibilidad.
- Eliminar código existente sin autorización.

---

# Verificación Obligatoria

Antes de finalizar un Sprint verificar:

□ Packages correctos.

□ Imports correctos.

□ Referencias válidas.

□ Dependencias completas.

□ Clases nuevas incluidas.

□ Clases modificadas incluidas.

□ AppContainer actualizado.

□ AndroidManifest actualizado (si corresponde).

□ Sin clases duplicadas.

□ Sin responsabilidades duplicadas.

□ Sin código muerto.

□ Sin TODO.

□ Sin pseudocódigo.

---

# Entrega

Entregar únicamente:

- Archivos creados.
- Archivos modificados.

Nunca entregar:

- Proyecto completo.
- Archivos sin cambios.
- Carpetas innecesarias.

Mantener exactamente la estructura de carpetas del proyecto.

---

# Integridad

Toda entrega debe ser autocontenida.

Si un archivo depende de otro:

Debe incluir ambos.

Nunca omitir archivos necesarios.

Nunca asumir que otro desarrollador los agregará.

---

# Arquitectura

Si durante el Sprint se detecta:

- contradicción
- dependencia circular
- duplicación
- arquitectura inconsistente

Debe detener inmediatamente la implementación.

Generar un informe técnico.

Esperar una decisión del CTO.

---

# Calidad

Todo código debe:

Compilar.

Ser consistente.

Mantener SOLID.

Mantener Clean Architecture.

Mantener MVVM.

Mantener escalabilidad.

---

# Definition of Done

Un Sprint solamente termina cuando:

✓ No existen imports rotos.

✓ No existen packages incorrectos.

✓ No existen referencias inexistentes.

✓ No faltan archivos.

✓ No existen dependencias omitidas.

✓ La entrega está lista para integrar.

✓ El CTO puede revisar la entrega sin solicitar archivos adicionales.