# THAMIS Cognitive Model v3.0

Este documento define la anatomía mental de THAMIS. Cada clase en el paquete `cognitive.model` representa un componente del razonamiento humano adaptado a la conducción de motocicletas.

## 1. Goal (Objetivo)
El usuario no emite comandos, busca alcanzar metas. 
- `Category`: Define el dominio (Música, Comunicación, etc.)
- `TargetState`: El estado final esperado (ej: "Spotify reproduciendo Duki").

## 2. Evidence (Evidencia)
Piezas de información que construyen la confianza.
- Posee un `expirationMs` para asegurar que el cerebro no use datos obsoletos (ej: la velocidad de hace 5 minutos no sirve para decidir ahora).

## 3. Risk (Riesgo)
El guardián de la seguridad. 
- Define qué tan seguros debemos estar antes de actuar. 
- Una llamada tiene un nivel de riesgo mucho mayor que subir el volumen.

## 4. WorldState vs MentalState
- `WorldState`: Los hechos fríos del mundo (sensores, batería, música).
- `MentalState`: El hilo de pensamiento (foco actual, memoria de experiencias pasadas).

## 5. Hypothesis & Evaluation
- `Hypothesis`: Una posibilidad estadística.
- `Evaluation`: El veredicto de esa posibilidad tras ser pesada contra el `Risk`.

## 6. ThamisDecision
El objeto final que se entrega al Actuator (ActionMapper). Incluye el `CognitiveTrace` para auditoría forense.
