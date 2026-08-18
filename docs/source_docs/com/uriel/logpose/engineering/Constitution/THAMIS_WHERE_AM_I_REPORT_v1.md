# Reporte de Inteligencia "Where Am I?"

## Estado: Implementado (Modo Shadow)

## Capacidades Cognitivas
THAMIS ahora puede mapear frases como "¿Dónde estoy?" a coordenadas geográficas y nombres de calles. El sistema evalúa la precisión del GPS antes de responder para evitar dar información errónea al motociclista.

## Pruebas de Estrés
Se han simulado escenarios con:
- Alta precisión (5m): Respuesta detallada.
- Baja precisión (>100m): Respuesta cautelosa.
- Sin señal: Aviso de pérdida de GPS.

## Próximos Pasos
- Integración con el proveedor de GPS de Android.
- Conexión con APIs de Puntos de Interés (POI) para búsquedas de gasolineras y hospitales.
