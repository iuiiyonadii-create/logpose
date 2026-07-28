# THAMIS CONVERSATION ENGINE v1.0

## 1. Mapa de Estados
| Estado | Descripción |
| :--- | :--- |
| IDLE | El sistema está en reposo. |
| LISTENING | Escuchando activamente la respuesta del usuario. |
| WAITING_CONFIRMATION | Se realizó una pregunta de Sí/No. |
| WAITING_CONTACT | Se requiere elegir entre varios contactos. |
| EXECUTING | La conversación concluyó en una acción autorizada. |
| FINISHED | Ciclo completado. |
| TIMEOUT | El usuario no respondió a tiempo. |

## 2. Flujo de Resolución
1. El sistema detecta una ambigüedad (ej: Dos Juanes).
2. Se genera una `PendingQuestion`.
3. El estado pasa a `WAITING_CONTACT`.
4. El usuario responde ("El primero").
5. `ConversationResolver` une "Juan" + "El primero" y dispara el comando.
