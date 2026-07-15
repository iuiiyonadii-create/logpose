# Cognitive Architecture

Version: 1.0

Status:
Official Specification

---

THAMIS no procesa comandos.

THAMIS interpreta el mundo.

Toda decisión comienza mediante observaciones.

Nunca mediante acciones.

El objetivo principal consiste en construir una representación interna del mundo suficientemente precisa para permitir decisiones coherentes.

THAMIS nunca responde directamente al usuario.

Primero comprende.

Luego razona.

Finalmente decide.
# Perception

Perception representa los sentidos de THAMIS.

Actualmente pueden existir múltiples fuentes.

Micrófono.

Bluetooth.

GPS.

Aplicaciones.

Calendario.

Internet.

Android.

Sensores.

Cada fuente observa únicamente una parte del mundo.

Ninguna fuente posee conocimiento completo.

Todas producen Evidence.

Nunca producen decisiones.

# Evidence

Evidence representa observaciones.

Ejemplos

"Bluetooth conectado."

"Spotify abierto."

"Usuario dijo 'poné música'."

"Velocidad 42 km/h."

Evidence nunca interpreta.

Nunca concluye.

Nunca supone.

Describe únicamente aquello que fue observado.

# Facts

Facts representan conclusiones simples.

Se construyen utilizando múltiples Evidence.

Ejemplo

Evidence

Bluetooth.

GPS.

Velocidad.

↓

Fact

Usuario probablemente conduciendo.

Otro ejemplo

Evidence

Spotify.

Auriculares.

↓

Fact

Usuario escuchando música.

Facts representan comprensión básica.

Todavía no representan razonamiento.
# State

State representa la fotografía completa del presente.

No describe un único hecho.

Describe la situación completa.

Ejemplo

Usuario conduciendo.

Escuchando música.

Utilizando navegación.

Lluvia.

Batería 30%.

Intercom conectado.

Este conjunto representa un único State.

Todas las decisiones utilizan State.

Nunca utilizan Evidence directamente.
# Knowledge

Knowledge representa aquello que THAMIS conoce acerca del mundo.

Ejemplos

Rock Nacional.

Spotify.

Bluetooth.

Android.

Clima.

Lenguaje.

Knowledge nunca describe usuarios.

Describe únicamente conocimiento general.

Knowledge permanece prácticamente constante.

Puede ampliarse.

Nunca cambia durante una conversación.
# Personal Knowledge

Representa aquello que THAMIS conoce acerca del usuario.

Ejemplos

Gustos.

Hábitos.

Rutinas.

Vocabulario.

Dispositivos.

Aplicaciones favoritas.

Personal Knowledge nunca abandona el dispositivo.

Nunca modifica Universal Knowledge.
# Reasoning

Reasoning representa el pensamiento de THAMIS.

Entrada

State.

Knowledge.

Personal Knowledge.

Policies.

Objetivo

Construir hipótesis.

Nunca ejecutar acciones.

Nunca aprender.

Nunca almacenar información.

Reasoning únicamente piensa.
# Hypothesis

Toda hipótesis representa una posible interpretación.

Nunca existe una única.

Siempre existen múltiples.

Cada hipótesis posee:

Confidence.

Evidence.

Facts.

Knowledge.

Memory.

Explanation.

Una hipótesis nunca ejecuta acciones.
# Decision

Decision representa la hipótesis seleccionada.

No representa la mejor confianza.

Representa la mejor decisión.

Toda decisión considera:

Policies.

Context.

Knowledge.

Memory.

Facts.

Confidence.

Una decisión puede descartar una hipótesis con mayor confianza si viola una Policy.
