# LogPose - Arquitectura

## Filosofía

LogPose está diseñado para ser:

- Liviano
- Modular
- Rápido
- Offline First
- Compatible desde Android 9

La prioridad es que funcione correctamente en teléfonos de gama baja utilizados por motociclistas y repartidores.

---

# Reglas

## 1. Una clase = una responsabilidad

Cada clase debe cumplir una única función.

---

## 2. Todo pasa por el Engine

Ningún módulo se comunica directamente con otro.

Siempre:

Módulo
↓
Engine
↓
Dispatcher
↓
Módulo

---

## 3. Un Ticket = una funcionalidad

Nunca mezclar varias funcionalidades en un mismo Ticket.

---

## 4. Siempre compilar

Cada Ticket debe terminar con:

- Build Successful
- Prueba en dispositivos
- Commit
- Push

---

## 5. Offline First

La IA mejora la aplicación.

No es obligatoria para usarla.

---

## 6. Performance

Objetivo:

- Bajo consumo de RAM
- Bajo consumo de batería
- Sin procesos innecesarios
- Sin servicios permanentes

---

## Compatibilidad

Android 9+

Dispositivo de referencia:

- Redmi 15C

Pruebas de compatibilidad:

- Samsung S8

---

## Arquitectura

Bluetooth

↓

Engine

↓

CommandDispatcher

↓

Voice

Music

Navigation

Phone

AI
# Arquitectura General

## Principio

La aplicación nunca debe depender directamente de Android.

Toda la lógica pasa por LogCore.

---

UI

↓

LogCore

↓

Services

↓

Providers

↓

Android

---

## Responsabilidades

UI

Presentación.

LogCore

Punto de entrada del framework.

Services

Reglas de negocio.

Providers

Comunicación con Android.

Platform

API del sistema operativo.

---

## Dependencias

UI conoce únicamente LogCore.

LogCore conoce Services.

Services conocen Providers.

Providers conocen Android.

Nunca al revés.