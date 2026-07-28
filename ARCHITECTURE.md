# THAMIS LAB - DOCUMENTO DE ARQUITECTURA MAESTRA

## 1. VISIÓN GENERAL
THAMIS es una plataforma autónoma de ingeniería diseñada para evolucionar **LogPose**. No es solo un banco de pruebas; es un ecosistema que investiga, simula y repara software para motociclistas.

## 2. FILOSOFÍA CENTRAL
- **Clean Architecture & SOLID**: El código debe ser una obra de ingeniería, no un parche.
- **Datos sobre Opiniones**: Cada decisión se basa en telemetría real y métricas científicas.
- **Resiliencia proactiva**: Buscamos el fallo antes de que el usuario lo sufra.

## 3. ARQUITECTURA DE ALTO NIVEL
```text
                THAMIS CLOUD / DISTRIBUTED
                         |
                MASTER ORCHESTRATOR
                         |
        -------------------------------------------
        |                |              |         |
   VOICE LAB        SIM ENGINE     CHAOS ENG    TASA (SEC)
        |                |              |         |
        -------------------------------------------
                         |
                 AI DECISION CORE (TADE)
                         |
               KNOWLEDGE GRAPH MEMORY
                         |
                  TRAINING PIPELINE
                         |
                   CLAUDE CODE BRIDGE
```

## 4. CATÁLOGO DE MÓDULOS
- **USO (Unified Simulation Orchestrator)**: Gestiona la carga de hardware físico.
- **VITE (Voice Intelligence Training)**: Mejora del motor NLP bajo ruido extremo.
- **AWS (Acoustic World Simulator)**: Recreación de climas y aerodinámica sonora.
- **TKGM (Knowledge Graph Memory)**: Memoria permanente de relaciones de ingeniería.
- **TDA (Distributed Lab)**: Escalabilidad masiva mediante nodos de simulación.

## 5. FLUJO DE DATOS Y EVENTOS
1. **Entrada**: Scenarios TSF, OSINT Findings, Logs de Usuarios Reales.
2. **Proceso**: Simulación en Workers -> Análisis RCA -> Debate Agentes -> Propuesta TADE.
3. **Salida**: Parches de Código, Documentación ADR, Certificados de Calidad.

## 6. PLAN DE ESCALABILIDAD
- **FASE 1 (Local)**: Laboratorio en PC de desarrollo (Actual).
- **FASE 2 (Híbrida)**: PC + Servidores dedicados para emulación masiva.
- **FASE 3 (Nube)**: Infraestructura elástica en AWS/Azure para pruebas de flota global.

## 7. CONSTITUCIÓN TÉCNICA
- **Límite de recursos**: < 80% utilización para garantizar fidelidad de simulación.
- **Documentación ADR**: Obligatoria para cada cambio arquitectónico.
- **Seguridad**: Cero exposición de datos personales de usuarios reales.
