# THAMIS NOTIFICATION - SISTEMA DE RESUMEN v1.0

## Lógica de Agrupación
THAMIS evita la repetición constante. Si llegan múltiples notificaciones en una ventana de 30 segundos, genera un resumen:
- "Tres mensajes nuevos de Juan."
- "Cinco notificaciones de varias aplicaciones."

## Resumen Contextual
El motor de resumen analiza si los mensajes son de la misma persona o grupo para ofrecer una respuesta consolidada:
- "Tenés novedades de tu familia." (Si se detectan alias favoritos).
