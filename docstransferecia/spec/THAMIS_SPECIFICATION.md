# THAMIS Technical Specification

Version: 1.0

Status: Living Specification

Author:
THAMIS Architecture Team

---

# Propósito

Este documento constituye la especificación oficial de THAMIS.

No describe una implementación.

Describe el comportamiento esperado.

Cualquier implementación futura deberá respetar esta especificación.

El código puede cambiar.

La arquitectura puede evolucionar.

La especificación representa el contrato oficial del proyecto.

---

# Objetivos

Definir completamente:

- Arquitectura.
- Componentes.
- Responsabilidades.
- Interfaces.
- Flujo cognitivo.
- Restricciones.
- Modelos.
- Algoritmos.
- Políticas.
- Reglas.

---

# Público objetivo

Arquitectos.

Desarrolladores.

Investigadores.

Inteligencias Artificiales.

Contribuidores.

---

# Definiciones

THAMIS

Motor Cognitivo Local.

LogPose

Cliente principal de THAMIS.

Research

Investigación.

Knowledge

Conocimiento estructurado.

Memory

Conocimiento personal.

Evidence

Hecho observado.

Hypothesis

Interpretación posible.

Decision

Interpretación seleccionada.

Experience

Resultado observado.

Learning

Actualización del conocimiento personal.
# System Goals

THAMIS deberá:

Comprender.

No únicamente responder.

---

Razonar.

No únicamente ejecutar.

---

Explicar.

No únicamente decidir.

---

Aprender.

No únicamente memorizar.

---

Adaptarse.

No únicamente configurarse.

---

Proteger.

No únicamente almacenar.
# Architectural Constraints

Nunca depender completamente de Internet.

Nunca depender completamente de un proveedor de IA.

Nunca depender completamente de LogPose.

Nunca depender de una implementación específica.

Nunca mezclar conocimiento universal con conocimiento personal.

Nunca ejecutar acciones críticas con confianza insuficiente.

Nunca aprender mediante una única interacción.
Perception

↓

Evidence

↓

Evidence Analysis

↓

Knowledge Query

↓

Hypothesis Generation

↓

Hypothesis Ranking

↓

Decision

↓

Execution

↓

Experience

↓

Learning

↓

Memory Update

Responsabilidad

Observar el mundo.

Entradas

Micrófono

Bluetooth

GPS

Sensores

Aplicaciones

Sistema

Calendario

Internet

No interpreta.

No decide.

Produce Evidence.
Evidence representa un hecho.

Todo hecho posee:

ID

Tipo

Origen

Valor

Confianza

Timestamp

Evidence nunca:

Razona.

Decide.

Aprende.
Responsabilidad

Analizar evidencias.

Debe:

Agrupar.

Eliminar duplicados.

Resolver conflictos.

Ordenar.

Validar.

Nunca genera hipótesis.
Responsabilidad

Responder preguntas.

Nunca decide.

Nunca aprende.

Nunca modifica conocimiento.

Debe:

Buscar.

Relacionar.

Consultar.

Indexar.
Todo conocimiento se representa mediante relaciones.

Ejemplo

Rock Nacional

↓

Artistas

↓

Contextos

↓

Décadas

↓

Popularidad

↓

Idioma

↓

Energía

↓

Estado de ánimo
Responsabilidad

Administrar conocimiento personal.

Debe almacenar:

Preferencias.

Hábitos.

Rutinas.

Vocabulario.

Dispositivos.

Correcciones.

Nunca modifica Knowledge.
# Intent Engine

## Objetivo

Determinar qué intenta hacer el usuario.

No interpreta entidades.

No toma decisiones.

No ejecuta acciones.

## Entrada

Texto normalizado.

## Salida

Una o más hipótesis de Intent.

## Responsabilidades

Detectar intención.

Calcular confianza inicial.

Permitir múltiples candidatos.

No depender del contexto.

## Nunca

Nunca ejecutar acciones.

Nunca consultar memoria.

Nunca aprender.
# Entity Engine

## Objetivo

Extraer información útil.

Ejemplos

Juan

Spotify

Rock Nacional

Ruta 2

Volumen

Playlist Favoritos

## Responsabilidades

Extraer entidades.

Clasificarlas.

Normalizarlas.

Asignar confianza.

## Nunca

Nunca generar decisiones.

Nunca interpretar contexto.
# Context Engine

## Objetivo

Construir el contexto actual.

## Fuentes

Bluetooth

GPS

Hora

Calendario

Aplicaciones

Sensores

Internet

Memoria

## Salida

ContextSnapshot

## Responsabilidades

Fusionar contexto.

Eliminar inconsistencias.

Actualizar cambios.
# Policy Engine

## Objetivo

Aplicar restricciones.

## Ejemplos

No llamar automáticamente.

No responder mensajes peligrosos.

No ejecutar acciones con baja confianza.

No distraer al conductor.

## Prioridad

Toda Policy tiene prioridad sobre cualquier hipótesis.
# Decision Engine

## Entrada

Hypotheses

Context

Policies

Knowledge

Memory

## Proceso

Evaluar.

Descartar.

Ordenar.

Seleccionar.

## Salida

Decision

## Regla

Siempre debe existir una explicación.
# Decision Engine

## Entrada

Hypotheses

Context

Policies

Knowledge

Memory

## Proceso

Evaluar.

Descartar.

Ordenar.

Seleccionar.

## Salida

Decision

## Regla

Siempre debe existir una explicación.
# Experience Engine

## Objetivo

Registrar el resultado real.

## Ejemplo

Usuario

↓

PLAY_MUSIC

↓

Correcto

↓

Experience

## Responsabilidades

Guardar experiencias.

Clasificarlas.

Determinar si sirven para aprender.

## Nunca

Modificar Memory directamente.
# Learning Engine

## Objetivo

Actualizar conocimiento personal.

## Entradas

Experience

Memory

Policies

## Salidas

Memory Update

## Reglas

Nunca modificar Universal Knowledge.

Nunca aprender automáticamente.

Toda actualización debe poder revertirse.
# Memory Engine

Tipos

Preferences

Habits

Devices

Vocabulary

Corrections

History

Routines

Favorite Apps

Favorite Music

Driving Profile

## Características

Persistente.

Local.

Privada.

Versionada.

Recuperable.
# Universal Knowledge

Representa conocimiento compartido.

Nunca pertenece a un usuario.

Ejemplos

Lenguaje.

Bluetooth.

Clima.

Música.

Android.

Navegación.

Llamadas.

Notificaciones.
# Personal Knowledge

Representa conocimiento exclusivo del usuario.

Ejemplos

Palabras propias.

Artistas favoritos.

Hábitos.

Rutinas.

Volumen habitual.

Intercom principal.

Aplicaciones preferidas.

Nunca modifica Universal Knowledge.
# Facts Engine

Version: 1.0

---

# Objetivo

Transformar múltiples evidencias en hechos de alto nivel.

Facts representan conclusiones.

No representan observaciones.

---

Evidence

↓

Facts

↓

Knowledge

---

Ejemplo

Evidence

Bluetooth conectado

Spotify abierto

GPS activo

Velocidad 45 km/h

↓

Fact

Usuario conduciendo.

---

Otro ejemplo

Evidence

Auriculares

Spotify

Casa

↓

Fact

Usuario escuchando música.

---

Responsabilidades

Fusionar evidencias.

Eliminar ruido.

Detectar estados.

Construir hechos.

---

Nunca

Tomar decisiones.

Aprender.

Modificar memoria.
# Reasoning Engine

Objetivo

Construir hipótesis utilizando:

Facts

Knowledge

Memory

Policies

Context

---

Responsabilidad

Pensar.

No ejecutar.

No aprender.

No almacenar.

---

Salida

Hypotheses
# Execution Engine

Responsabilidad

Ejecutar únicamente decisiones aprobadas.

Nunca interpreta.

Nunca razona.

Nunca aprende.

Ejemplos

Abrir Spotify.

Llamar contacto.

Enviar mensaje.

Responder llamada.

Controlar volumen.
# Suggestion Engine

Responsabilidad

Realizar sugerencias.

Nunca ejecutar automáticamente.

Ejemplos

¿Querés reproducir Rock Nacional?

¿Querés llamar nuevamente?

¿Querés abrir navegación?
# Conversation Engine

Objetivo

Mantener conversaciones.

No reemplaza Reasoning.

Conversation utiliza Reasoning.

Nunca al revés.

Conversation administra:

Contexto conversacional.

Confirmaciones.

Preguntas.

Respuestas.

Correcciones.
# Skill Engine

Una Skill representa una capacidad.

Ejemplos

Music

Bluetooth

Calls

Weather

Navigation

Notifications

Smart Home

Cada Skill es independiente.

Todas utilizan el mismo motor cognitivo.
# Runtime

El Runtime representa la implementación ejecutándose.

Debe ser:

Ligero.

Offline.

Escalable.

Rápido.

Seguro.
# Compiler

Actualmente no implementado.

Objetivo futuro.

Transformar:

Research

↓

Knowledge

↓

Runtime

El teléfono nunca deberá interpretar archivos Markdown.

Todo conocimiento llegará previamente compilado.
# Research

Research constituye la fuente de conocimiento.

Research nunca pertenece al Runtime.

Research nunca se distribuye al usuario.

Research sirve para construir Knowledge.
# Plugins

Arquitectura futura.

Objetivo.

Permitir agregar nuevas Skills.

Sin modificar el núcleo.

Ejemplo

Plugin Música.

Plugin Clima.

Plugin Casa Inteligente.

Plugin Vehículos.

Plugin Salud.
# Testing

Cada módulo deberá poseer:

Unit Test.

Integration Test.

Performance Test.

Stress Test.

Reasoning Test.

Learning Test.

Regression Test.
# Versioning

THAMIS utiliza:

Semantic Versioning.

Mayor.

Menor.

Patch.

La documentación evoluciona junto al código.

Nunca por separado.
Perception

↓

Evidence

↓

Evidence Analyzer

↓

Facts

↓

Knowledge Query

↓

Reasoning

↓

Hypothesis

↓

Decision

↓

Execution

↓

Experience

↓

Learning

↓

Memory
# State Engine

Version: 1.0

---

# Objetivo

Construir una representación completa del estado actual.

State representa la mejor descripción posible del presente.

No representa una decisión.

No representa una hipótesis.

Representa el mundo según THAMIS.

---

Flujo

Evidence

↓

Facts

↓

State

---

Ejemplos

State

Usuario conduciendo.

Usuario escuchando música.

Usuario utilizando navegación.

Usuario en llamada.

Usuario descansando.

Usuario trabajando.

Usuario entrenando.

---

Responsabilidades

Fusionar Facts.

Eliminar contradicciones.

Mantener consistencia.

Actualizar cambios.

---

Nunca

Ejecutar acciones.

Aprender.

Tomar decisiones.
# Reasoning Pipeline

Pipeline oficial.

Perception

↓

Evidence

↓

Evidence Analyzer

↓

Facts

↓

State

↓

Knowledge Query

↓

Memory Query

↓

Hypothesis Generator

↓

Hypothesis Ranking

↓

Policy Validation

↓

Decision

↓

Execution

↓

Experience

↓

Learning

↓

Memory Update

Todo THAMIS deberá respetar este flujo.
# Confidence

La confianza representa la probabilidad de que un dato sea correcto.

Nunca representa importancia.

Nunca representa prioridad.

---

Rango

0.0

↓

1.0

---

0.95

Prácticamente seguro.

---

0.80

Muy probable.

---

0.60

Probable.

---

0.50

Dudoso.

Solicitar confirmación.

---

Menor a 0.50

Nunca ejecutar acciones críticas.
# Explainability

Toda decisión debe poder responder:

¿Qué observó?

¿Qué hechos construyó?

¿Qué estado detectó?

¿Qué conocimiento utilizó?

¿Qué memoria utilizó?

¿Qué hipótesis descartó?

¿Por qué eligió la decisión final?

Toda explicación debe poder reconstruirse.
# Knowledge Graph

Todo conocimiento se representa mediante relaciones.

Ejemplo

Rock Nacional

↓

Artistas

↓

Soda Stereo

↓

Canciones

↓

Persiana Americana

↓

Década

↓

80

↓

País

↓

Argentina

↓

Estado de ánimo

↓

Energía Alta

---

Nunca almacenar conocimiento aislado.

Siempre relacionarlo.
# Personal Graph

Cada usuario posee un grafo propio.

Nunca compartido.

Ejemplo

Uriel

↓

Rock Nacional

↓

Conducir

↓

La Renga

↓

Volumen 7

↓

Intercom Principal

↓

08:00

Todo aprendizaje modifica este grafo.

Nunca modifica Universal Knowledge.
# Skills

Una Skill representa una capacidad.

Ejemplos

Music

Calls

Bluetooth

Weather

Navigation

Notifications

Smart Home

Calendar

Vehicle

Cada Skill implementa únicamente su dominio.

Nunca implementa razonamiento.
# Universal Rules

Toda Skill utiliza:

Evidence.

Facts.

State.

Knowledge.

Memory.

Policies.

Decision.

No existen excepciones.
# Dependency Rules

Perception

↓

Evidence

↓

Facts

↓

State

↓

Reasoning

↓

Decision

↓

Execution

Nunca permitir dependencias inversas.

Nunca permitir ciclos.

Nunca permitir acoplamiento entre Skills.
# Performance

Objetivo.

Todo el razonamiento básico deberá ejecutarse localmente.

Tiempo objetivo.

Menos de 100 ms.

Uso mínimo de memoria.

Consumo mínimo de batería.
# Offline

THAMIS debe funcionar completamente sin Internet.

Internet representa únicamente una mejora.

Nunca una dependencia.

Todas las funciones críticas deberán ejecutarse offline.
# AI Integration

Los modelos de IA representan herramientas.

No representan el núcleo del sistema.

Toda integración con IA debe respetar:

Privacidad.

Explicabilidad.

Control del usuario.

Funcionamiento offline cuando sea posible.

THAMIS nunca dependerá completamente de un LLM.
# Memory Architecture

Version: 1.0

---

La memoria constituye uno de los pilares de THAMIS.

No existe una única memoria.

Existen múltiples memorias especializadas.

Cada una posee responsabilidades diferentes.

Nunca deben mezclarse.

---

Memory

↓

Episodic Memory

Semantic Memory

Preference Memory

Procedural Memory

Context Memory

Device Memory

Conversation Memory

Temporary Memory
# Episodic Memory

Representa experiencias.

Ejemplos

Ayer reprodujo Rock Nacional.

Hace dos horas llamó a Juan.

El domingo abrió Spotify.

No representa gustos.

Representa hechos históricos.

Puede eliminarse.

Puede comprimirse.

Nunca modifica directamente Semantic Memory.
# Semantic Memory

Representa conocimiento aprendido.

Ejemplos

El usuario suele escuchar Rock Nacional.

Prefiere Spotify.

Conduce principalmente por la mañana.

Se construye utilizando múltiples experiencias.

Nunca mediante una sola experiencia.
# Preference Memory

Representa gustos.

Ejemplos

Idioma.

Volumen.

Música.

Aplicación favorita.

Intercom favorito.

Modo oscuro.

Las preferencias pueden ser:

Configuradas.

Aprendidas.

Confirmadas.
# Procedural Memory

Representa rutinas.

Ejemplos

Conectar intercom.

↓

Abrir Spotify.

↓

Abrir navegación.

↓

Volumen 7.

Las rutinas deben poder modificarse.

Nunca ejecutarse automáticamente sin autorización.
# Context Memory

Relaciona comportamiento con contexto.

Ejemplos

Moto.

↓

Rock Nacional.

Trabajo.

↓

LoFi.

Casa.

↓

Podcast.
# Device Memory

Dispositivos conocidos.

Intercom principal.

Auriculares.

Auto.

Moto.

Parlantes.

Configuraciones específicas.

Volúmenes.

Perfiles.
# Conversation Memory

Representa únicamente la conversación actual.

No es permanente.

Puede descartarse.

Nunca modifica Memory directamente.

Debe convertirse primero en Experience.
# Temporary Memory

Información utilizada únicamente durante el razonamiento.

Se destruye al finalizar la decisión.

Nunca se guarda.

Nunca se aprende.
# Knowledge Architecture

Knowledge

↓

Domain

↓

Category

↓

Concept

↓

Relations

↓

Rules

↓

Examples

Todo conocimiento deberá poder relacionarse.

Nunca almacenar listas aisladas.
# Knowledge Query

Responsabilidad.

Responder preguntas.

Ejemplos.

¿Qué artistas pertenecen al Rock Nacional?

¿Qué aplicaciones reproducen música?

¿Qué significa "rola"?

No modifica conocimiento.
# Knowledge Rule

Una Rule representa conocimiento.

Nunca representa código.

Ejemplo

SI

Usuario dice "rola"

ENTONCES

Song

No representa una decisión.
# Knowledge Relation

Toda relación posee.

Origen.

Destino.

Tipo.

Peso.

Confianza.

Fuente.

Fecha.
Perception

↓

Evidence

↓

Facts

↓

State

↓

Knowledge Query

↓

Memory Query

↓

Reasoning

↓

Hypothesis

↓

Decision

↓

Execution

↓

Experience

↓

Learning

↓

Memory Update
Nunca razonar utilizando una sola evidencia.

Siempre utilizar múltiples evidencias.

Toda hipótesis debe indicar:

Qué evidencias utilizó.

Qué conocimiento utilizó.

Qué memoria utilizó.

Qué políticas evaluó.
# Experience Database

Version: 1.0

---

## Objetivo

Conservar el historial de experiencias utilizadas por THAMIS.

Experience Database nunca representa conocimiento.

Representa únicamente hechos históricos.

---

## Flujo

Execution

↓

Experience

↓

Experience Database

↓

Learning

↓

Memory

---

## Beneficios

Permite:

- Auditar decisiones.

- Explicar aprendizajes.

- Detectar errores.

- Revertir conocimiento.

- Detectar cambios de comportamiento.

---

## Nunca

Nunca modificar Knowledge.

Nunca ejecutar acciones.

Nunca generar hipótesis.
# State Model

State representa la mejor descripción posible del presente.

State no representa una decisión.

State representa la realidad según THAMIS.

---

Ejemplo

Estado

Conduciendo

Moto

Lluvia

Spotify

Intercom

GPS

↓

Driving State

---

Todo State posee

ID

Confidence

Facts

Timestamp

Duration
# World Model

THAMIS mantiene una representación del mundo.

Ejemplos

Tiempo.

Ubicación.

Aplicaciones.

Dispositivos.

Conectividad.

Conducción.

Llamadas.

Todo forma parte del World Model.

El usuario forma parte del World Model.

Nunca al revés.
# User Model

Representa al usuario.

Incluye

Preferencias.

Rutinas.

Hábitos.

Historial.

Lenguaje.

Dispositivos.

Perfil de conducción.

Aplicaciones favoritas.

Nivel de experiencia.

Nunca almacena información innecesaria.
# Session Model

Representa la sesión actual.

Comienza cuando THAMIS inicia.

Finaliza cuando THAMIS termina.

La Session contiene

Conversation

Temporary Memory

Current State

Temporary Facts

Temporary Evidence
# Domain Model

Cada dominio representa un área de conocimiento.

Ejemplos

Music

Calls

Navigation

Bluetooth

Weather

Calendar

Notifications

Vehicle

Cada dominio posee

Research

Knowledge

Rules

Entities

Vocabulary
# Vocabulary

Cada dominio mantiene su vocabulario.

Ejemplo

Music

Tema

Canción

Rola

Track

Single

Playlist

Álbum

Disco

Nunca mezclar vocabularios.
# Language Model

THAMIS comprende lenguaje.

No comandos.

No keywords.

Comprende relaciones.

Sinónimos.

Expresiones.

Regionalismos.

Errores.

Abreviaciones.
# Regional Knowledge

THAMIS adapta lenguaje según región.

Ejemplo

Argentina

Tema

México

Rola

España

Canción

El conocimiento regional pertenece a Universal Knowledge.

No a Personal Knowledge.
# Personal Vocabulary

Representa palabras propias del usuario.

Ejemplo

"Poneme un temita."

"Mandá música."

"Lo de siempre."

Estas expresiones pertenecen únicamente al usuario.
# Localization

Localization no representa traducción.

Representa adaptación cultural.

Ejemplos

Expresiones.

Palabras.

Costumbres.

Música.

Servicios.

Emergencias.

Normativas.
# Knowledge Packs

El conocimiento podrá distribuirse mediante paquetes.

Ejemplos

Argentina Pack

México Pack

España Pack

Brasil Pack

Cada Pack contiene

Research

Knowledge

Vocabulary

Rules

Localization
# SDK Vision

THAMIS podrá integrarse mediante SDK.

Android.

Linux.

Automóviles.

Wearables.

IoT.

Robótica.

No dependerá de LogPose.
# Plugin System

Toda Skill será un Plugin.

Plugin Music.

Plugin Weather.

Plugin Calls.

Plugin Navigation.

Plugin Calendar.

Plugin Smart Home.

El núcleo nunca dependerá de Plugins.

Los Plugins dependen del núcleo.
# Final Principles

Arquitectura antes que código.

Research antes que implementación.

Evidence antes que hipótesis.

Facts antes que decisiones.

Explicación antes que aprendizaje.

Privacidad antes que comodidad.

Local antes que nube.

Conocimiento antes que IA.

Usuario antes que negocio.
