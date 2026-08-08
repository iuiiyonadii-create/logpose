# 🛰️ THAMIS Mission Control: Live Snapshot

Este es un renderizado estático del panel de control tal como se vería en ejecución. Puedes ver el **Espejo Virtual de THAMIS** reaccionando a un parche de auto-reparación fonética.

<div style="background: #070a0f; color: #e0f7fc; font-family: 'Consolas', monospace; padding: 20px; border: 2px solid #00f2ff33; border-radius: 8px;">

  <!-- Top Bar -->
  <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #00f2ff33; padding-bottom: 10px; margin-bottom: 20px;">
    <h2 style="margin: 0; color: #00f2ff;">THAMIS LAB MISSION CONTROL OS</h2>
    <div style="display: flex; gap: 10px;">
      <span style="background: #0e1722; color: #00f2ff; border: 1px solid #00f2ff; padding: 5px 15px; border-radius: 4px; font-size: 12px; font-weight: bold;">LINKED: adb-emulator-5554</span>
      <button style="background: #00f2ff; color: #000; border: none; padding: 5px 15px; border-radius: 4px; font-weight: bold; cursor: pointer;">ENGINE ACTIVE</button>
    </div>
  </div>

  <div style="display: flex; gap: 20px; height: 500px;">

    <!-- Sidebar: Telemetry -->
    <div style="width: 250px; background: #0d131a; padding: 15px; border-radius: 4px; border: 1px solid #00f2ff33;">
      <div style="margin-bottom: 20px;">
        <div style="font-size: 11px; opacity: 0.7;">VELOCIDAD GPS SIMULADA</div>
        <div style="font-size: 24px; color: #00f2ff; font-weight: bold;">124 km/h</div>
      </div>
      <div style="margin-bottom: 20px;">
        <div style="font-size: 11px; opacity: 0.7;">RUIDO AMBIENTAL (STRESS)</div>
        <div style="font-size: 24px; color: #ff3300; font-weight: bold;">88%</div>
      </div>
      <div style="margin-bottom: 20px;">
        <div style="font-size: 11px; opacity: 0.7;">VOCABULARIO OPTIMIZADO</div>
        <div style="font-size: 24px; color: #00ff00; font-weight: bold;">14 Reglas</div>
      </div>
      <div style="margin-bottom: 10px; color: #00ff00; font-weight: bold;">[SELF-HEALING LOG]</div>
      <div style="font-size: 10px; color: #00ff00; background: #051005; padding: 5px; height: 180px; overflow: hidden; border: 1px solid #004400;">
        > THAMIS Engine: READY<br>
        > Stress applied: HIGHWAY_STORM<br>
        > Phonetic mismatch detected: 'acdc'<br>
        > Repair generated: PHONETIC_ALIAS<br>
        > Validating in Headless Engine...<br>
        > Validation PASSED (98.2%)<br>
        > Patch Approved by Release Gate<br>
        > GITHUB: Pushed to origin/bugfix/voice-opt<br>
        > UI_ACTION: Launching Spotify...
      </div>
    </div>

    <!-- Main Viewport: Virtual Phone -->
    <div style="flex: 1; background: #040609; border: 1px solid #00f2ff33; border-radius: 4px; display: flex; flex-direction: column; align-items: center; justify-content: center; position: relative;">
      <div style="position: absolute; top: 10px; left: 10px; color: #00f2ff; font-size: 12px; font-weight: bold;">| ESPEJO VIRTUAL THAMIS</div>

      <!-- Virtual Device -->
      <div style="width: 200px; height: 400px; border: 8px solid #333; border-radius: 20px; background: #000; position: relative; display: flex; flex-direction: column;">
        <div style="flex: 1; margin: 8px; background: #111; border-radius: 10px; display: flex; flex-direction: column; align-items: center; justify-content: center;">
          <div style="padding: 3px 10px; background: rgba(0,242,255,0.2); border: 1px solid #00f2ff; border-radius: 10px; font-size: 9px; color: #00f2ff; margin-bottom: 20px;">ACTIVE: SPOTIFY</div>
          <!-- App Icon (Spotify) -->
          <div style="width: 60px; height: 60px; background: #1DB954; border-radius: 15px; box-shadow: 0 0 20px rgba(29,185,84,0.4);"></div>
          <div style="margin-top: 15px; font-size: 10px; color: #00ff00; font-weight: bold;">VALIDATING PATCH...</div>
        </div>
      </div>

    </div>
  </div>
</div>

### 🛠️ Cómo ejecutarlo en tu PC:
Para ver la interfaz real interactuando con tus dispositivos ADB, ejecuta este comando en la terminal de Android Studio:

```bash
./gradlew :ui:mission-control:run
```

Este comando lanzará la ventana de **Mission Control** en tu escritorio, escaneará tus dispositivos conectados y activará el espejo virtual.
