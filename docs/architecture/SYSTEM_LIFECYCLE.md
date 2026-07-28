# Especificación del Ciclo de Vida del Sistema (LogPose)

## Principio de Diseño: La Invisibilidad
El usuario nunca debe conocer la máquina de estados interna de LogPose. Los estados existen únicamente para la implementación. La interfaz de voz solo comunica información útil para conducir. LogPose nunca anuncia cambios internos de estado; solo habla cuando aporta valor real al conductor.

---

## 1. Estado OFF
**Descripción:** LogPose está completamente inactivo.
- **Condiciones:** Bluetooth del dispositivo guardado no conectado.
- **Acciones:**
    - No escucha comandos.
    - No reproduce audio ni genera mensajes.
    - Espera conexión Bluetooth del dispositivo vinculado.

## 2. Estado CONNECTING
**Entrada:** Se detecta conexión Bluetooth del dispositivo guardado.
- **Acciones:**
    - Verificar coincidencia de MAC.
    - Validar disponibilidad de perfiles (A2DP/HFP).
    - Inicializar módulos internos.
- **Audio:** Ninguno durante el proceso. Al finalizar con éxito: `"LogPose conectado"`.
- **Transición:** Inmediatamente pasa a **READY**.

## 3. Estado READY
**Descripción:** Estado principal de vigilia.
- **Acciones:**
    - Escucha permanentemente el comando de activación ("Log...").
    - No habla ni realiza acciones por iniciativa propia.
    - No reproduce sonidos innecesarios.
- **Audio:** Silencio absoluto.

## 4. Estado ACTIVE
**Entrada:** El usuario invoca LogPose (Ej: "LogPose...").
- **Respuesta:** `"Te escucho."`
- **Acción:** Abre ventana de escucha para el comando.

## 5. Estado EXECUTING
**Entrada:** Recibe un comando válido.
- **Acciones:** Ejecuta la acción específica (Llamar, Navegar, Música).
- **Audio:** Solo lo necesario para confirmar la acción (Ej: `"Llamando a Juan"`, `"Volumen ocho"`).
- **Finalización:** Al terminar, **NO anuncia el regreso a espera**. Vuelve internamente a **READY** en silencio.

## 6. Estado DISCONNECTED
**Evento:** Se pierde la conexión Bluetooth.
- **Audio:** Solo si ocurre durante una sesión activa (Ej: `"Intercomunicador desconectado"`).
- **Acción:** Si el sistema estaba en READY prolongado, cierra la sesión silenciosamente.

---

*Este documento es la base de ingeniería para todos los módulos de LogPose.*
