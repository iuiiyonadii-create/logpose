# LogPose Engineering Rules

1. Una clase = una responsabilidad.

2. Toda la UI habla únicamente con Repositories.

3. El corazón de LogPose es LogPoseService.

4. Los estados usan StateFlow.

5. No usar IA cuando un algoritmo local puede resolver el problema.

6. Ningún módulo conoce la implementación interna de otro.

7. Todo módulo debe poder probarse por separado.

8. Toda nueva función debe tener un lugar claro dentro de la arquitectura.

9. No crear clases "por las dudas".

10. Antes de escribir código, pensar dónde debe vivir.