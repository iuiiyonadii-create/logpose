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

Pendiente de reevaluación.