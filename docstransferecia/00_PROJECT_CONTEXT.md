# THAMIS Project Context

Version: 1.0

Status: Living Document

Last Updated: 2026

---

# Introducción

Este documento representa la memoria oficial del proyecto THAMIS.

No es un README.

No es una guía rápida.

No es un documento comercial.

Su objetivo es permitir que cualquier desarrollador, arquitecto o sistema de inteligencia artificial pueda comprender completamente el proyecto sin necesidad de conocer el historial de conversaciones que llevaron a su creación.

Toda decisión importante deberá quedar registrada aquí.

Toda modificación importante deberá respetar este documento.

Este archivo debe evolucionar junto con THAMIS.

---

# ¿Qué es THAMIS?

THAMIS es un Motor Cognitivo Local diseñado inicialmente para LogPose.

Su función principal consiste en comprender el lenguaje natural del usuario, analizar el contexto, construir hipótesis, tomar decisiones explicables y aprender únicamente a partir de evidencia suficiente.

THAMIS no depende de Internet para funcionar.

La inteligencia principal reside en el dispositivo del usuario.

Los servicios externos pueden ampliar capacidades, pero nunca reemplazar el funcionamiento básico del sistema.

---

# ¿Qué NO es THAMIS?

THAMIS no es un chatbot.

THAMIS no es un modelo de lenguaje.

THAMIS no es un asistente basado únicamente en IA.

THAMIS no depende de un proveedor específico como OpenAI, Google o cualquier otro.

THAMIS no almacena información personal en servidores.

THAMIS no vende información del usuario.

THAMIS no crea perfiles comerciales.

THAMIS no aprende sin evidencia.

---

# Historia

El proyecto comenzó como una aplicación llamada LogPose.

LogPose fue concebida como un asistente de voz para motociclistas, especialmente repartidores, capaz de controlar funciones del teléfono sin que el usuario necesitara quitar las manos del manillar.

El objetivo principal siempre fue aumentar la seguridad durante la conducción.

Con el tiempo quedó claro que la parte más compleja del proyecto no era controlar Bluetooth ni abrir aplicaciones.

El verdadero desafío era comprender correctamente al usuario.

A partir de esa necesidad nació THAMIS.

Inicialmente THAMIS era solamente un módulo interno.

Sin embargo, durante el diseño de la arquitectura se tomó la decisión de convertirlo en un sistema independiente.

Desde ese momento LogPose dejó de ser el centro de la arquitectura.

Actualmente la relación entre ambos proyectos es la siguiente:

THAMIS es un motor cognitivo.

LogPose es una aplicación que utiliza ese motor.

Esto implica que cualquier otra aplicación podría utilizar THAMIS en el futuro.

La independencia entre ambos proyectos constituye una decisión arquitectónica fundamental.

---

# Visión

La visión del proyecto consiste en construir un motor cognitivo capaz de comprender el lenguaje natural sin depender permanentemente de servicios en la nube.

THAMIS debe poder funcionar completamente offline.

Debe ser modular.

Debe ser explicable.

Debe ser extensible.

Debe ser mantenible durante muchos años.

Cada nueva funcionalidad deberá integrarse respetando estos principios.

---

# Objetivos

Los objetivos principales son:

- Comprender lenguaje natural.
- Analizar contexto.
- Construir evidencia.
- Generar múltiples hipótesis.
- Tomar decisiones explicables.
- Aprender únicamente cuando exista evidencia suficiente.
- Proteger completamente la privacidad del usuario.
- Ejecutarse localmente.
- Mantener independencia tecnológica.

Ningún objetivo funcional podrá violar estos principios.

---

# Filosofía

THAMIS fue diseñado siguiendo varios principios fundamentales.

El primero es Local First.

Siempre que sea posible, toda operación deberá ejecutarse localmente.

Internet constituye una mejora.

Nunca un requisito.

El segundo principio es Privacy by Design.

La privacidad no es una característica opcional.

Forma parte de la arquitectura.

Toda decisión debe proteger la información personal.

El tercer principio consiste en la explicabilidad.

Toda decisión tomada por THAMIS debe poder explicarse.

Si una decisión no puede justificarse mediante evidencia, debe considerarse incorrecta.

El cuarto principio establece que THAMIS nunca supone.

THAMIS aprende.

Y únicamente aprende cuando existe evidencia suficiente.

---

# Relación con LogPose

THAMIS nació dentro de LogPose.

Sin embargo, ambas arquitecturas evolucionan de forma independiente.

LogPose depende de THAMIS.

THAMIS no depende de LogPose.

Esta decisión permite reutilizar THAMIS en futuros proyectos sin necesidad de modificar el motor.

LogPose actúa como cliente del motor cognitivo.

No como propietario del mismo.
****
# THAMIS Project Context

Version: 1.0

Status: Living Document

---

# Introducción

Este documento representa la memoria oficial del proyecto THAMIS.

Debe contener toda la información necesaria para comprender el proyecto sin depender del historial de conversaciones.

Cualquier desarrollador, arquitecto o inteligencia artificial deberá poder continuar el desarrollo únicamente leyendo esta documentación.

Este documento tiene prioridad sobre cualquier comentario dentro del código.

Cuando exista una contradicción entre el código y esta documentación, deberá analizarse cuál representa realmente la arquitectura esperada y corregirse la inconsistencia.

---

# Historia

THAMIS nació durante el desarrollo de LogPose.

LogPose comenzó como una aplicación Android para motociclistas cuyo objetivo principal era permitir controlar el teléfono mediante voz sin distraerse de la conducción.

Durante las primeras versiones quedó claro que controlar Bluetooth, reproducir música o responder llamadas no era el problema principal.

El verdadero desafío consistía en comprender correctamente al usuario.

Inicialmente se evaluó utilizar asistentes existentes o depender completamente de servicios de inteligencia artificial.

Esa idea fue descartada.

Se decidió construir un motor propio capaz de comprender lenguaje natural respetando tres principios fundamentales:

- funcionamiento local;
- privacidad;
- independencia tecnológica.

Así nació THAMIS.

---

# ¿Qué es THAMIS?

THAMIS es un Motor Cognitivo Local.

No es únicamente un asistente.

No es únicamente una inteligencia artificial.

No es únicamente un módulo de LogPose.

THAMIS representa una arquitectura completa capaz de:

- comprender lenguaje;
- interpretar contexto;
- construir evidencias;
- generar hipótesis;
- tomar decisiones;
- aprender con evidencia;
- adaptarse al usuario.

---

# ¿Qué NO es THAMIS?

THAMIS no es un chatbot.

THAMIS no es un modelo de lenguaje.

THAMIS no depende de OpenAI.

THAMIS no depende de Google.

THAMIS no depende de Internet.

THAMIS no vende información.

THAMIS no comparte datos personales.

THAMIS no crea perfiles comerciales.

THAMIS no aprende por accidente.

THAMIS no ejecuta acciones sin suficiente confianza.

---

# Relación con LogPose

LogPose utiliza THAMIS.

THAMIS no depende de LogPose.

La aplicación puede cambiar.

El motor cognitivo permanece.

Toda decisión arquitectónica deberá favorecer la independencia entre ambos proyectos.

En el futuro THAMIS podrá utilizarse desde otras aplicaciones sin modificaciones importantes.

---

# Objetivo Principal

Construir el mejor motor cognitivo local para dispositivos Android.

El objetivo no consiste únicamente en ejecutar comandos.

El objetivo consiste en comprender correctamente la intención real del usuario.

La ejecución de acciones representa solamente la consecuencia final del razonamiento.

---

# Filosofía General

THAMIS fue diseñado siguiendo una filosofía muy clara.

Comprender antes que responder.

Pensar antes que ejecutar.

Explicar antes que aprender.

Aprender antes que memorizar.

La arquitectura siempre tendrá prioridad sobre la velocidad de implementación.

No se aceptarán soluciones rápidas que comprometan el futuro del proyecto.

---

# Principios

## Local First

Toda funcionalidad importante deberá ejecutarse localmente.

Internet representa una mejora.

Nunca un requisito.

---

## Privacy by Design

La privacidad forma parte de la arquitectura.

No constituye una característica opcional.

Toda información personal pertenece únicamente al usuario.

---

## Explainability

Toda decisión deberá poder explicarse.

Cada decisión debe indicar qué evidencias fueron utilizadas.

Cada decisión debe indicar por qué fue elegida.

---

## Learning with Evidence

THAMIS nunca supone.

THAMIS aprende.

Y únicamente aprende cuando existe evidencia suficiente.

Una única interacción nunca crea conocimiento.

---

## Universal Knowledge

Representa conocimiento general.

Ejemplos:

- música;
- clima;
- navegación;
- lenguaje;
- Bluetooth.

Nunca pertenece a un usuario específico.

---

## Personal Knowledge

Representa conocimiento exclusivo del usuario.

Ejemplos:

- preferencias;
- hábitos;
- vocabulario;
- rutinas;
- dispositivos habituales.

Nunca abandona el dispositivo.

Nunca modifica el conocimiento universal.

---

# Arquitectura Cognitiva

THAMIS razona siguiendo un flujo fijo.

Perception

↓

Evidence

↓

Evidence Analysis

↓

Knowledge

↓

Hypothesis

↓

Decision

↓

Experience

↓

Learning

↓

Memory

Cada etapa posee una única responsabilidad.

Ninguna etapa puede asumir responsabilidades de otra.

---

# Responsabilidades

Perception

Obtiene información del mundo.

Ejemplos:

- voz;
- Bluetooth;
- GPS;
- hora;
- aplicaciones;
- sensores.

---

Evidence

Representa hechos.

No interpreta.

No decide.

No razona.

Simplemente describe información disponible.

---

Evidence Analysis

Organiza las evidencias.

Agrupa.

Filtra.

Elimina duplicados.

Detecta conflictos.

# Knowledge

El conocimiento representa información estructurada utilizada por THAMIS para razonar.

El conocimiento nunca representa decisiones.

El conocimiento nunca representa memoria.

El conocimiento describe el mundo.

Ejemplos:

- Música
- Bluetooth
- Clima
- Navegación
- Contactos
- Aplicaciones
- Lenguaje

Todo conocimiento pertenece al motor.

Nunca pertenece al usuario.

---

# Memory

La memoria representa conocimiento personal.

No describe el mundo.

Describe al usuario.

Ejemplos:

- Artistas favoritos

- Volumen habitual

- Intercom principal

- Aplicación de navegación preferida

- Forma habitual de hablar

- Rutinas

Toda la memoria permanece exclusivamente dentro del dispositivo.

Nunca será enviada a servidores.

---

# Experience

Una experiencia representa el resultado de una interacción.

Ejemplo

Usuario

↓

Poné música

↓

THAMIS interpreta correctamente

↓

Usuario no corrige

↓

Experience

↓

Learning

Una experiencia incorrecta nunca debe convertirse automáticamente en aprendizaje.

---

# Learning

El aprendizaje modifica únicamente el conocimiento personal.

Nunca modifica el conocimiento universal.

Nunca aprende mediante una sola ejecución.

Nunca aprende utilizando hipótesis inseguras.

Toda modificación debe poder justificarse.

---

# Evidence

Evidence constituye el idioma universal de THAMIS.

Todos los módulos producen Evidence.

Ejemplos

Speech

↓

Evidence

Bluetooth

↓

Evidence

Hora

↓

Evidence

GPS

↓

Evidence

Spotify

↓

Evidence

Memory

↓

Evidence

Knowledge

↓

Evidence

Gracias a esto todos los módulos permanecen desacoplados.

Ningún módulo conoce la implementación interna de otro.

---

# Evidence Analysis

El análisis de evidencias posee una única responsabilidad.

Transformar un conjunto desordenado de evidencias en información organizada.

Ejemplos

Agrupar evidencias.

Eliminar duplicados.

Resolver conflictos.

Ordenar por relevancia.

Detectar inconsistencias.

El análisis todavía no toma decisiones.

---

# Hypothesis

Una hipótesis representa una posible interpretación.

THAMIS nunca genera una única hipótesis.

Siempre intenta construir varias alternativas.

Ejemplo

Usuario

↓

"Poné algo"

Hipótesis A

PLAY_MUSIC

0.93

Hipótesis B

PLAY_PLAYLIST

0.72

Hipótesis C

RESUME

0.61

La decisión todavía no existe.

Solo existen posibilidades.

---

# Decision

La decisión representa la hipótesis finalmente elegida.

Toda decisión debe:

Ser segura.

Ser explicable.

Respetar las políticas.

Considerar el contexto.

Considerar el conocimiento.

Considerar la memoria.

Toda decisión debe poder reconstruirse posteriormente.

---

# Confidence

La confianza representa el grado de seguridad.

No representa calidad.

No representa importancia.

Representa únicamente la probabilidad de que una interpretación sea correcta.

La confianza nunca constituye el único criterio para decidir.

---

# Context

El contexto representa el estado actual del usuario y del dispositivo.

Ejemplos

Conduciendo

Moto

Lluvia

Auriculares

Intercom

Casa

Trabajo

Spotify abierto

Batería baja

Hora

Ubicación

El contexto modifica las decisiones.

Nunca modifica el lenguaje.

---

# Intent

El Intent representa el objetivo del usuario.

No representa palabras.

No representa frases.

No representa sintaxis.

Representa únicamente lo que el usuario intenta lograr.

Ejemplo

Usuario

"Poné música."

Intent

PLAY_MUSIC

---

# Entity

Las entidades representan información.

No representan acciones.

Ejemplos

Soda Stereo

Rock Nacional

Juan

Spotify

Playlist Favoritos

Ruta 2

Las entidades enriquecen una hipótesis.

Nunca crean una hipótesis por sí mismas.

---

# Research

Toda nueva capacidad deberá comenzar mediante investigación.

Nunca se agregará conocimiento directamente al código.

Primero

Research

↓

Knowledge

↓

Código

Nunca al revés.

---

# Documentación

Toda decisión importante deberá documentarse.

El conocimiento no puede depender de conversaciones.

Toda arquitectura deberá poder reconstruirse leyendo únicamente la carpeta docs/.

# Forma de Trabajo

La metodología de desarrollo de THAMIS forma parte del proyecto.

No depende del desarrollador.

Toda nueva conversación deberá respetar estas reglas.

---

## Roles

El usuario actúa como:

- Product Owner
- Fundador del proyecto
- Responsable de las decisiones estratégicas.

La inteligencia artificial actúa como:

- Arquitecto
- Tech Lead
- Senior Kotlin Developer
- Investigador
- Revisor de arquitectura

La IA deberá tomar todas las decisiones técnicas menores.

El usuario únicamente decidirá cuestiones que afecten el futuro del proyecto.

---

# Metodología

El desarrollo se organiza mediante Tickets.

Cada Ticket debe contener:

Objetivo

Archivos

Código completo

Compilación

Commit sugerido

Nunca entregar fragmentos incompletos.

Siempre entregar archivos completos.

---

# Comunicación

Las respuestas deben ser breves.

Explicar únicamente cuando sea necesario.

Priorizar:

Código.

Arquitectura.

Decisiones.

No escribir texto innecesario.

---

# Decisiones

Cuando el usuario responde:

"Sí"

Debe interpretarse como:

Aprobado.

No volver a preguntar.

Continuar con el desarrollo.

---

# Calidad del Código

Todo archivo debe cumplir:

Una única responsabilidad.

Bajo acoplamiento.

Alta cohesión.

Código legible.

Código mantenible.

Sin dependencias innecesarias.

Sin código muerto.

Sin soluciones temporales permanentes.

---

# Principios SOLID

Toda nueva arquitectura deberá respetar SOLID.

Si alguna implementación existente viola SOLID deberá planificarse su refactorización.

---

# Investigación

Antes de crear conocimiento nuevo deberá existir investigación.

Research

↓

Knowledge

↓

Código

Nunca escribir reglas arbitrarias.

Nunca inventar lenguaje.

Siempre utilizar lenguaje real.

---

# Privacidad

Toda información personal pertenece únicamente al usuario.

Nunca:

Enviar datos.

Vender datos.

Crear perfiles comerciales.

Compartir preferencias.

Sin consentimiento explícito.

---

# Aprendizaje

THAMIS aprende únicamente mediante evidencia.

Nunca aprende:

Por una única acción.

Por un error.

Por una hipótesis insegura.

Por información incompleta.

---

# Explicabilidad

Toda decisión debe responder:

¿Por qué?

¿Qué evidencias utilizó?

¿Qué hipótesis descartó?

¿Qué conocimiento utilizó?

Si no puede responder esas preguntas, la decisión debe considerarse incorrecta.

---

# Qué nunca debe hacerse

Nunca acoplar módulos innecesariamente.

Nunca conectar módulos directamente.

Todos los módulos deben comunicarse mediante modelos comunes.

Actualmente ese modelo común es:

Evidence

---

Nunca mezclar:

Research

Knowledge

Runtime

Cada uno posee responsabilidades diferentes.

---

Nunca romper compatibilidad sin una razón arquitectónica importante.

---

Nunca optimizar prematuramente.

Primero:

Arquitectura.

Después:

Optimización.

---

Nunca agregar complejidad innecesaria.

La solución más simple que respete la arquitectura siempre tendrá prioridad.

---

# Estado actual

Actualmente THAMIS se encuentra en transición.

Existe una arquitectura antigua.

Existe una arquitectura nueva.

El objetivo inmediato consiste en reemplazar completamente la arquitectura antigua.

---

Actualmente existen los siguientes módulos base:

Evidence

Hypothesis

Intent

Knowledge

Research

Decision

Learning

Memory

Policy

Context

Confidence

---

El próximo objetivo consiste en construir:

Evidence Analyzer

Knowledge Engine

Decision Engine

Experience Engine

Learning Engine

Memory Engine


