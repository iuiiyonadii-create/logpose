# THAMIS NOTIFICATION INTELLIGENCE v1.0

## Propósito
Este dominio se encarga de convertir el flujo caótico de notificaciones del sistema en una experiencia auditiva estructurada, segura y relevante para el motociclista.

## Capas
- **Classifier**: Identifica de qué trata la notificación (Mensaje, Sistema, Navegación, etc.).
- **Priority Engine**: Asigna niveles de urgencia (Critical a Silent).
- **Safety Gate**: Filtra las notificaciones según la velocidad, ruido y estado de la comunicación (ej: no leer mensajes durante llamadas).
- **Shadow Mode**: Orquesta el procesamiento sin intervenir en el hardware real de audio.
- **Audit**: Traza completa de cada decisión para análisis posterior.

## Reglas de Conducción
- **> 120 km/h**: Solo se permiten notificaciones de categoría EMERGENCY o CRITICAL.
- **Llamada Activa**: Todo queda en espera (Action.WAIT) excepto alertas de seguridad.
- **Detección de Spam**: Filtros semánticos para ignorar ofertas o contenido no relevante.
