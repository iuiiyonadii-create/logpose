# ADR-001 - Usuario Objetivo

**Estado:** Aprobado

**Versión:** 1.0

**Fecha:** 13/07/2026

**Proyecto:** LogPose

---

# Contexto

Durante la etapa inicial del proyecto se evaluó qué tipo de usuario debía ser el principal objetivo de LogPose.

Inicialmente se consideró diseñar una aplicación compatible con cualquier motociclista y cualquier dispositivo.

Luego del análisis del mercado argentino se concluyó que esa estrategia aumentaría la complejidad del proyecto sin aportar beneficios reales para la primera versión.

---

# Decisión

LogPose será desarrollado principalmente para repartidores urbanos argentinos.

Todas las decisiones técnicas y de producto deberán priorizar las necesidades de este usuario antes que las de cualquier otro perfil.

---

# Usuario objetivo

## Tipo de usuario

- Repartidores urbanos.
- Motociclistas que utilizan la moto como herramienta de trabajo.
- Conductores que necesitan operar el teléfono completamente mediante voz.

---

## Motocicletas habituales

- Honda Wave
- Honda GLH
- Honda XR150
- Rouser
- Boxer
- Motomel
- Zanella
- Gilera

En general, motocicletas entre 110 cc y 300 cc.

---

## Teléfonos prioritarios

- Redmi
- Motorola
- Samsung Serie A
- Poco

---

## Intercomunicadores prioritarios

- EJEAS V6 Pro+
- EJEAS Q8
- Q58 Max
- FreedConn

Cardo y Sena serán compatibles cuando sea posible, pero no representan la prioridad para la Beta.

---

## Aplicaciones prioritarias

### Navegación

- Google Maps
- Waze

### Música

- Spotify
- YouTube Music
- VLC
- Poweramp
- Musicolet

### Mensajería

- WhatsApp
- Instagram (Mensajes)
- Telegram

---

# Principios derivados

Todas las funciones deberán responder afirmativamente a la siguiente pregunta:

> ¿Esta función mejora realmente la experiencia del repartidor mientras conduce?

Si la respuesta es negativa, la función no será prioritaria para la Beta.

---

# Consecuencias

Esta decisión modifica las prioridades del proyecto.

A partir de este momento:

- Se investigarán primero los dispositivos más utilizados por el usuario objetivo.
- Se priorizará la compatibilidad con hardware de gama media.
- Se investigarán primero EJEAS antes que Cardo o Sena.
- La experiencia del repartidor tendrá prioridad sobre funcionalidades avanzadas para usuarios ocasionales.

---

# Justificación

El objetivo de LogPose no es construir la aplicación con más funciones.

El objetivo es construir la aplicación más útil para el usuario que realmente la necesita.

Diseñar para un usuario específico permite tomar mejores decisiones de arquitectura, simplificar la interfaz y aumentar la calidad de la experiencia de uso.

---

# Estado

✅ Decisión aprobada.

Esta decisión se considera estable y servirá como referencia para el resto del proyecto.