# THAMIS Driving Voice Experience Engine v1.0

## 1. Visión General
El motor de experiencia vocal es la capa encargada de humanizar la interacción entre THAMIS y el motociclista. Su objetivo principal es decidir no solo el contenido del mensaje, sino el **momento** y el **estilo** adecuados para minimizar la distracción del conductor.

## 2. Componentes Clave
- **DrivingVoiceExperienceEngine**: Orquestador principal de la decisión vocal.
- **VoicePriorityEngine**: Gestiona la jerarquía de mensajes (Emergencia > Navegación > Multimedia).
- **InterruptionManager**: Filtra mensajes según la carga cognitiva y el estado de la comunicación.
- **ResponseStyleEngine**: Adapta el texto a versiones cortas o detalladas según la velocidad.
- **SilenceManager**: Garantiza ventanas de silencio para evitar la fatiga auditiva.

## 3. Reglas de Interacción
- **Mínima Distracción**: Si el conductor va a más de 120km/h, el estilo pasa automáticamente a `EMERGENCY` (mensajes ultra-cortos y directos).
- **Respeto a la Conversación**: No se emiten mensajes secundarios si el usuario está hablando o en una llamada.
- **Fluidez**: Se calcula un retraso dinámico (`VoiceTiming`) para que las instrucciones no se pisen entre sí.

## 4. Estándares
- **100% Kotlin Puro**.
- **Context-Aware**: Consume datos del `WorldSnapshot` para cada decisión.
