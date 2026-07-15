# LEGACY: Pre-engineering/ Architecture Documents

Archived from: app/src/main/java/docs/architecture/ARCHITECTURE.md
and app/src/main/java/docs/Knowledge/ideas/ServiceResolver.md

Status: superseded by engineering/. Preserved because it correctly
anticipated the Provider concept later formalized in
engineering/01_LogCore/Providers/ARCHITECTURE.md.

------------------------------------------------------------------------------
## Original: docs/architecture/ARCHITECTURE.md
```
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

Nunca al revés.```

------------------------------------------------------------------------------
## Original: docs/Knowledge/ideas/ServiceResolver.md
```
# ServiceResolver

Estado:
En espera

Prioridad:
Media

Creado:
2026-07-13

---

## Objetivo

Desacoplar ActionEngine de los Services concretos.

En lugar de que ActionEngine conozca MusicService,
CallService o BluetoothService, un ServiceResolver
determina qué Service debe ejecutar una Action.

---

## Ventajas

- Engine más limpio.
- Fácil agregar nuevos Services.
- Sigue el principio Open/Closed.
- Facilita una futura arquitectura basada en registro de servicios.

---

## Desventajas

- Agrega una capa adicional.
- Agrega una llamada extra.
- Con pocas capacidades no aporta beneficios reales.
- Devuelve objetos que luego requieren resolución adicional.

---

## Motivo por el que no entra en la Beta

Actualmente LogCore posee muy pocos Services.

El costo de mantener esta capa es mayor que el beneficio.

---

## Reconsiderar cuando

- Existan más de 8 Services.
- El ActionEngine empiece a crecer demasiado.
- El mantenimiento del Engine deje de ser sencillo.

---

Estado

Pendiente de reevaluación.```
