# THAMIS Trust & Transparency Engine Architecture v1.0

## 1. Visión General
El motor de confianza es la capa de THAMIS responsable de eliminar el efecto "caja negra" del asistente. Su función es explicar las decisiones cognitivas (por qué se calló, por qué interrumpió, por qué cambió de ruta) de forma que el usuario pueda comprender y confiar en la lógica del sistema.

## 2. Componentes Clave
- **DecisionExplanationEngine**: Traduce los motivos técnicos y los resultados del `SafetyGate` en frases humanizadas para el conductor.
- **ReasoningTraceManager**: Mantiene una traza auditable de qué reglas se aplicaron a cada evento.
- **UserControlManager**: Centraliza el control del usuario sobre el aprendizaje y la proactividad.
- **ConfidenceManager**: Gestiona la transparencia sobre la veracidad de los datos de sensores (GPS/BT).

## 3. Filosofía de Transparencia
LogPose no debe ser un misterio. Si el sistema ignora un mensaje de WhatsApp, el usuario tiene derecho a saber que fue porque estaba por doblar en una esquina peligrosa. Esta información construye una relación de confianza a largo plazo.

## 4. Garantía de Privacidad
Toda explicación se genera localmente. Los logs de razonamiento son efímeros (en sesión) y el usuario puede borrarlos o limitar su persistencia mediante el `PrivacyController`.
