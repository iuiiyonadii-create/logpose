# THAMIS DIALOG ENGINE v1.0 - ARQUITECTURA

## 1. Propósito
El motor de diálogo es el encargado de gestionar las interacciones verbales de varios pasos entre THAMIS y el usuario. A diferencia de un sistema de un solo comando, el Dialog Engine permite desambiguaciones, confirmaciones y correcciones en tiempo real.

## 2. Componentes Clave
- **ConversationState**: Una máquina de estados determinística (IDLE -> WAITING -> EXECUTING).
- **ConfirmationEngine**: Intérprete especializado en respuestas afirmativas y negativas.
- **DialogContext**: Mantiene la memoria viva de la conversación actual.
- **ConversationResolver**: Une las respuestas parciales del usuario con el contexto previo.

## 3. Lógica de Seguridad
- **Timeout**: Las conversaciones expiran automáticamente a los 20 segundos de inactividad para evitar distracciones residuales.
- **Determinismo**: No utiliza IA generativa; las rutas de diálogo son fijas y seguras.
- **Integración**: Se conecta con el `WorldContext` para saber si es seguro preguntar.

## 4. Estándar de Implementación
- 100% Kotlin Puro.
- Cero dependencias Android.
- Operación en Shadow Mode para auditoría.
