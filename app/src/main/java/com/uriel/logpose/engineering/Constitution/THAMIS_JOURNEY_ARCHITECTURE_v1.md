# THAMIS Journey Intelligence Engine Architecture v1.0

## 1. Visión General
El motor de inteligencia de viaje es la capa encargada de dar significado semántico a los recorridos físicos. A diferencia del motor de estados de viaje (que solo detecta movimiento), este componente comprende el *propósito* y el *patrón* detrás del desplazamiento.

## 2. Componentes Clave
- **JourneyContextAnalyzer**: Determina si el viaje es un trayecto al trabajo, una entrega de delivery o un viaje recreativo largo.
- **JourneyPatternAnalyzer**: Detecta recurrencias en rutas y horarios para anticipar preferencias (ej. música habitual en cierta zona).
- **JourneyInsightEngine**: Transforma los patrones detectados en sugerencias útiles y no intrusivas.
- **JourneyPrivacyController**: Garantiza que el usuario tenga el control absoluto sobre qué historial se registra y qué se olvida.

## 3. Filosofía de Acompañamiento
THAMIS no debe rastrear, debe acompañar. El motor genera "Insights" que se presentan como recomendaciones opcionales, nunca como acciones forzadas, respetando la autonomía del conductor.

## 4. Garantía de Privacidad
- **Local-Only**: Todo el análisis de patrones se realiza en el núcleo cognitivo del dispositivo.
- **User-Gated**: El historial circular (100 sesiones) puede ser eliminado o desactivado por el usuario en cualquier momento.
