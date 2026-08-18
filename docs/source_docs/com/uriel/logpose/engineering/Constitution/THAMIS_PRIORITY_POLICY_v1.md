# THAMIS ORCHESTRATION - POLÍTICA DE PRIORIDAD v1.0

## 1. Jerarquía de Dominios (Base)
| Dominio | Prioridad Base |
| :--- | :---: |
| EMERGENCY | 1000 |
| SAFETY | 900 |
| NAVIGATION | 800 |
| COMMUNICATION | 700 |
| SYSTEM | 600 |
| MULTIMEDIA | 500 |
| INFORMATION | 400 |

## 2. Ajustes Dinámicos
- **Alta Velocidad (>100km/h)**: 
  - Navegación: +100
  - Multimedia: -200
- **Riesgo Crítico**:
  - Solo se permiten acciones de tipo EMERGENCY.
  - El resto de prioridades se calculan como 0.
