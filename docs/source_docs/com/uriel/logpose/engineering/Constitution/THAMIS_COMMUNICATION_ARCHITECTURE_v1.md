# THAMIS Communication Architecture v1.0

## Propósito
Gobernar toda la inteligencia cognitiva relacionada con llamadas, mensajes y notificaciones, priorizando siempre la seguridad del motociclista.

## Capas
- **Resolver**: Intérprete de lenguaje natural y resolución de identidades (Contactos/Grupos).
- **Evaluator**: Sistema ponderado de evidencias para determinar la confianza cognitiva.
- **Safety Gate**: Filtro de seguridad basado en velocidad y estado del mundo.
- **Memory**: Aprendizaje de hábitos y contactos frecuentes.
- **Shadow Mode**: Controlador de simulación y auditoría sin ejecución real.

## Flujo de Decisión
1. Entrada de Voz -> `CommunicationIntentResolver`.
2. Identidad Detectada -> `CommunicationEntityResolver`.
3. Cálculo de Confianza -> `CommunicationEvidenceEvaluator`.
4. Validación de Seguridad -> `CommunicationSafetyGate`.
5. Registro en Auditoría -> `CommunicationTrace`.

## Estándares
- 100% Kotlin Puro.
- Cero dependencias Android.
- Cumplimiento estricto de Shadow Mode.
