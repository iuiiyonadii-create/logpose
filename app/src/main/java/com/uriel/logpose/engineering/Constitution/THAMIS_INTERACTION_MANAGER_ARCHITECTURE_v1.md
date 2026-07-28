# THAMIS v3.3 - Interaction Manager Architecture

## 1. Visión General
El Interaction Manager actúa como el "Árbitro de Interrupciones" de LogPose. Su objetivo es evitar la fatiga cognitiva del conductor organizando quién tiene permiso para hablar y cuándo.

## 2. Componentes Críticos
- **InteractionQueue**: Una cola bloqueante basada en prioridad dinámica.
- **InteractionPolicy**: Aplica los perfiles de conducción (Delivery, Ruta, Ciudad) que modifican la relevancia de los mensajes.
- **InteractionTrace**: Un log detallado para auditar cada decisión de silencio.

## 3. Flujo de Decisión
1. Recepción de `InteractionRequest`.
2. Cálculo de prioridad efectiva según el `DrivingMode`.
3. Verificación de `Cooldown` (Silencio post-charla).
4. Arbitraje:
   - ¿Prioridad > Prioridad Actual? -> **EXECUTE** (Interrumpe).
   - ¿Prioridad < Prioridad Actual? -> **QUEUE** (Espera su turno).
   - ¿Es idéntico a un evento previo reciente? -> **MERGE** (Agrupa).

## 4. Garantía de Seguridad
El módulo es 100% Pure Kotlin, garantizando que el orquestador sea determinista y testeable sin necesidad de hardware Android.
