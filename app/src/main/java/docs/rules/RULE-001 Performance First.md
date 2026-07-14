# RULE-001 — Performance First

## Estado

Vigente

---

# Objetivo

LogPose debe utilizar la menor cantidad posible de recursos sin comprometer la estabilidad, la seguridad ni la experiencia del usuario.

El rendimiento no es una optimización de último momento. Es un requisito de diseño.

---

# Principio

Cada componente debe cumplir una única responsabilidad utilizando la menor cantidad posible de:

- CPU
- RAM
- Batería
- Objetos
- Hilos
- Procesos
- Permisos
- Dependencias

---

# Reglas

## 1. Una responsabilidad

Cada clase debe existir por un único motivo.

Si una clase necesita dos motivos para existir, debe dividirse.

---

## 2. No crear trabajo innecesario

Antes de escribir código preguntarse:

- ¿Puede evitar ejecutarse?
- ¿Puede reutilizar una instancia?
- ¿Puede hacerse con menos memoria?
- ¿Puede hacerse con menos objetos?
- ¿Puede hacerse con menos llamadas?

Si la respuesta es sí, se rediseña.

---

## 3. No optimizar por intuición

Toda optimización debe responder a un problema real o a un requisito del proyecto.

No se agregará complejidad sin una ventaja clara.

---

## 4. Framework liviano

LogCore debe ser un framework pequeño.

Toda nueva capa debe justificar su existencia.

---

## 5. Android económico como referencia

Todo el desarrollo debe considerar dispositivos de gama baja como objetivo principal.

Si funciona correctamente allí, funcionará mejor en dispositivos superiores.

---

# Pregunta obligatoria

Antes de crear cualquier clase:

> ¿Existe una forma más simple y más liviana de resolver este problema?

Si existe, debe elegirse esa solución.

---

# Resultado esperado

Un framework:

- pequeño
- rápido
- mantenible
- eficiente
- preparado para largas jornadas de uso