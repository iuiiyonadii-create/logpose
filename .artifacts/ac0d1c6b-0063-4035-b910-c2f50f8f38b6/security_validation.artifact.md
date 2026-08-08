# Guía de Validación de Seguridad: Integración App-PC

Este documento describe cómo verificar el nuevo protocolo de seguridad en el sabor `lab` de LogPose.

## 1. Verificación de Firma en Comandos Salientes (App -> PC)
Cuando la App envía un comando a la PC (ej: `RIDER_ONLINE`), ahora incluye la API Key.

**Cómo probar:**
1. Iniciar un viaje en la variante `lab`.
2. Observar los logs (Logcat) filtrando por `PCBridgeLabImpl`.
3. Verás un log similar a: `PCBridgeLabImpl: Sent secured command to 192.168.1.33:9999`.
4. El paquete UDP real tendrá este formato: `LOGPOSE_THAMIS_v1_SECURE:RIDER_ONLINE:¡Listo para el reparto!`

## 2. Verificación de Protección en Comandos Entrantes (PC -> App)
El `PCControlServer` ahora rechaza cualquier comando que no empiece con la API Key válida.

**Simulación de Ataque (Comando sin Firma):**
Desde una terminal en la PC (o usando `adb shell` si tienes herramientas UDP):
```bash
echo "SIM_DEGRADATION" | nc -u -w1 <IP_DEL_CELULAR> 5051
```
*   **Resultado esperado**: El Logcat mostrará: `PCControlServer: Unauthorized command attempt.` y la acción será ignorada.

**Simulación de Comando Autorizado:**
```bash
echo "LOGPOSE_THAMIS_v1_SECURE:SIM_DEGRADATION" | nc -u -w1 <IP_DEL_CELULAR> 5051
```
*   **Resultado esperado**: El Logcat mostrará: `PCControlServer: Authorized remote command -> SIM_DEGRADATION` y se disparará la simulación de degradación.

## 3. Aislamiento de Producción
**Verificación:**
1. Instalar la variante `production`.
2. Intentar enviar un broadcast al `TrainingReceiver`:
   ```bash
   adb shell am broadcast -a com.uriel.logpose.INJECT_COGNITIVE_TEST --es text "test"
   ```
3. **Resultado esperado**: El comando fallará o no hará nada porque el Receiver no existe en el APK de producción. Además, `PCBridge` será un No-Op, por lo que no habrá tráfico UDP hacia la PC.

> [!CAUTION]
> Nunca compartas el `THAMIS_API_KEY` definido en `NetworkConfig.kt` fuera del entorno de desarrollo seguro.
