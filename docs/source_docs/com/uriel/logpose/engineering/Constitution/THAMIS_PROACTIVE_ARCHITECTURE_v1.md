# THAMIS Proactive Assistant Engine Architecture v1.0

## 1. Visión General
El motor proactivo es la capa encargada de iniciar comunicaciones de forma autónoma cuando THAMIS detecta que puede aportar valor sin distraer al conductor. Su función es pasar de un sistema reactivo (responde a órdenes) a uno proactivo (anticipa necesidades).

## 2. Componentes Clave
- **SuggestionEngine**: Genera ideas de interacción basadas en el `WorldModel` (ej. batería baja, tráfico, hábitos).
- **ProactiveDecisionEngine**: Evalúa si la sugerencia es lo suficientemente importante y urgente.
- **CommunicationTimingEngine**: Decide si *ahora* es un buen momento físico para hablar (velocidad estable, sin giros próximos).
- **ProactivePrivacyController**: Permite al usuario limitar o desactivar la proactividad del sistema.

## 3. Filosofía de No-Intrusión
La regla de oro es: **"Silencio > Interrupción innecesaria"**. Si el sistema tiene dudas sobre el valor de una sugerencia o si el conductor está bajo una carga cognitiva alta, el motor proactivo debe callar.

## 4. Garantía de Seguridad
- **Low-Risk Only**: Las sugerencias proactivas nunca disparan acciones de control del vehículo o multimedia de forma forzada; siempre se presentan como recomendaciones opcionales.
- **100% Kotlin Puro**: Lógica desacoplada de los disparadores de Android.
