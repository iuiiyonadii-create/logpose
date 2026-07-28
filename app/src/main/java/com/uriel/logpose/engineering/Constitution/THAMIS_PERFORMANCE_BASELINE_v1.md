# THAMIS Performance Baseline v1.0

## 1. Objetivos de Tiempo (SLA Interno)
| Operación | Objetivo (Target) | Límite Crítico |
| :--- | :---: | :---: |
| Inicio de Core | 1.5s | 3.0s |
| Respuesta de Voz | 300ms | 1000ms |
| Generación de Plan | 50ms | 200ms |
| Sincronía del Mundo | 10ms | 50ms |

## 2. Gestión de Memoria
- El núcleo cognitivo debe operar con un máximo de **5MB** de RAM dedicada en estados de reposo.
- Durante picos de planificación masiva, no debe superar los **10MB**.

## 3. Impacto de Batería
THAMIS debe mantener un consumo promedio inferior a **150mA** durante el uso activo del intercomunicador, garantizando jornadas de 8+ horas.
