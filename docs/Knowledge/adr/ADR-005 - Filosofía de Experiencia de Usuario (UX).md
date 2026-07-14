# ADR-005 - Filosofía de Experiencia de Usuario (UX)

**Estado:** Aprobado

**Versión:** 1.0

**Fecha:** 13/07/2026

**Proyecto:** LogPose

---

# Contexto

Desde el inicio del proyecto surgió una idea que se mantuvo constante durante todas las conversaciones:

El usuario de LogPose no desea aprender una aplicación nueva.

Quiere subir a la moto, conectar su intercomunicador y comenzar a trabajar.

La aplicación debe adaptarse al conductor y no al contrario.

---

# Problema

Muchas aplicaciones ofrecen una gran cantidad de configuraciones, menús y opciones.

Aunque son potentes, generan complejidad, requieren aprendizaje y obligan al usuario a dedicar tiempo antes de poder utilizarlas correctamente.

En un contexto de conducción esto resulta contraproducente.

---

# Decisión

LogPose priorizará siempre la simplicidad sobre la cantidad de funciones.

La aplicación deberá ser intuitiva, comprensible desde el primer uso y requerir la menor cantidad posible de configuración por parte del usuario.

---

# Principios

## Configurar una sola vez.

Toda configuración importante deberá realizarse durante la instalación o en el primer uso.

Posteriormente la aplicación recordará automáticamente las preferencias del usuario.

---

## La aplicación debe enseñar sola.

El usuario no debería necesitar leer manuales ni tutoriales para utilizar LogPose.

La interfaz deberá explicar naturalmente cada función.

---

## Menos opciones, mejores decisiones.

Agregar más configuraciones no significa ofrecer una mejor experiencia.

Cada opción deberá justificar su existencia.

Si una configuración no aporta valor real al usuario, deberá eliminarse.

---

## Lo importante primero.

Las funciones utilizadas con mayor frecuencia deberán encontrarse siempre al alcance del usuario.

Las configuraciones avanzadas existirán, pero permanecerán fuera del flujo principal.

---

## Coherencia.

Todas las pantallas deberán comportarse de manera consistente.

El usuario nunca debería preguntarse dónde encontrar una función.

---

## Priorizar el contexto de conducción.

Toda decisión de diseño deberá considerar que el usuario puede utilizar la aplicación con guantes, bajo la lluvia, con ruido ambiente y sin poder mirar constantemente la pantalla.

---

## La voz es la interfaz principal.

Siempre que sea posible, el usuario deberá poder realizar una acción utilizando comandos de voz en lugar de interactuar con la pantalla.

---

## La interfaz debe desaparecer.

La mejor experiencia de usuario es aquella en la que el conductor deja de pensar en la aplicación y simplemente conduce.

---

# Justificación

Una interfaz simple reduce errores, disminuye el tiempo de aprendizaje y mejora la seguridad durante la conducción.

El objetivo no consiste en desarrollar una aplicación con más funciones que otras.

El objetivo consiste en desarrollar una aplicación que resulte natural de utilizar incluso para personas con poca experiencia tecnológica.

---

# Consecuencias

A partir de esta decisión:

- Se limitará la cantidad de configuraciones visibles.
- Se priorizarán asistentes de configuración inicial.
- Se utilizarán perfiles recomendados cuando sea posible.
- Se minimizarán las acciones necesarias para completar una tarea.
- Las nuevas funcionalidades deberán integrarse sin aumentar innecesariamente la complejidad de la interfaz.

---

# Impacto en el proyecto

Este ADR afecta directamente a:

- Diseño de interfaz.
- Configuración inicial.
- Sistema de perfiles.
- Asistente por voz.
- Notification Engine.
- Music Engine.
- Navegación.
- Futuras funcionalidades.

---

# Principio Fundamental

> "La mejor interfaz es la que el conductor no necesita mirar."

---

# Estado

✅ Decisión aprobada.