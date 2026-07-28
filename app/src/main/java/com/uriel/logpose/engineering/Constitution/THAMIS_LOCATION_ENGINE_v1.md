# THAMIS Location Engine v1.0

## Propósito
El motor de inteligencia de ubicación permite a THAMIS comprender y responder consultas sobre la posición actual del conductor sin necesidad de iniciar una navegación activa.

## Componentes
- **CurrentLocation**: Modelo puro que representa las coordenadas y datos geográficos.
- **LocationResolver**: Intérprete de lenguaje natural para identificar intenciones de búsqueda de ubicación.
- **LocationFormatter**: Generador de respuestas naturales ("Estás en...", "No tengo señal...").
- **LocationValidation**: Calcula la confianza basada en la precisión del GPS y el movimiento.

## Intenciones Soportadas
- `WHERE_AM_I`: Ubicación general.
- `WHAT_CITY`: Identificación de ciudad.
- `WHAT_STREET`: Identificación de calle.
- `NEAREST_GAS`: Búsqueda de estaciones de servicio.
- `NEAREST_PARKING`: Búsqueda de estacionamiento.
- `NEAREST_HOSPITAL`: Búsqueda de centros médicos.
- `NEAREST_POLICE`: Búsqueda de comisarías.
