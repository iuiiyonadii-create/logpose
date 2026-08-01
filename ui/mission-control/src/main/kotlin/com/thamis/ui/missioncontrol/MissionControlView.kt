package com.thamis.ui.missioncontrol

import com.thamis.lab.performance.adb.AdbManager
import com.thamis.lab.performance.device.DeviceInfo

/**
 * Modern Cyber-Themed Mission Control HTML/CSS UI Generator with styled ComboBox selector.
 */
public object MissionControlView {

    public fun generateDashboardHtml(devices: List<DeviceInfo>, selectedSerial: String?): String {
        val optionsHtml = if (devices.isEmpty()) {
            """<option value="" disabled selected>NO SE DETECTARON DISPOSITIVOS</option>"""
        } else {
            devices.joinToString("\n") { dev ->
                val selected = if (dev.deviceId == selectedSerial) "selected" else ""
                """<option value="${dev.deviceId}" $selected>${dev.displayName}</option>"""
            }
        }

        return """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <title>THAMIS LAB — MISSION CONTROL OS</title>
            <style>
                :root {
                    --bg-dark: #070a0f;
                    --panel-bg: #0d131a;
                    --accent-teal: #00f2ff;
                    --accent-teal-dark: #008891;
                    --text-primary: #e0f7fc;
                    --border-cyan: #00f2ff33;
                }
                body {
                    margin: 0;
                    padding: 0;
                    background-color: var(--bg-dark);
                    color: var(--text-primary);
                    font-family: 'Consolas', 'Segoe UI', monospace;
                }
                .top-bar {
                    display: flex;
                    justify-content: flex-end;
                    align-items: center;
                    padding: 12px 24px;
                    background-color: #030508;
                    border-bottom: 1px solid var(--border-cyan);
                    gap: 16px;
                }
                /* Custom Styled Cyber ComboBox */
                .device-selector {
                    appearance: none;
                    -webkit-appearance: none;
                    background-color: #0e1722;
                    color: var(--accent-teal);
                    border: 1px solid var(--accent-teal);
                    border-radius: 4px;
                    padding: 8px 36px 8px 12px;
                    font-size: 13px;
                    font-weight: bold;
                    letter-spacing: 0.5px;
                    cursor: pointer;
                    background-image: url('data:image/svg+xml;utf8,<svg fill="%2300f2ff" height="24" viewBox="0 0 24 24" width="24" xmlns="http://www.w3.org/2000/svg"><path d="M7 10l5 5 5-5z"/></svg>');
                    background-repeat: no-repeat;
                    background-position: right 8px center;
                    outline: none;
                    box-shadow: 0 0 10px rgba(0, 242, 255, 0.2);
                    transition: all 0.3s ease;
                }
                .device-selector:hover, .device-selector:focus {
                    box-shadow: 0 0 15px rgba(0, 242, 255, 0.5);
                    border-color: #ffffff;
                }
                .device-selector option {
                    background-color: #0b121a;
                    color: #ffffff;
                    padding: 10px;
                }
                .btn-start {
                    background-color: transparent;
                    color: var(--accent-teal);
                    border: 1px solid var(--accent-teal);
                    padding: 8px 20px;
                    font-weight: bold;
                    letter-spacing: 1px;
                    text-transform: uppercase;
                    cursor: pointer;
                    transition: all 0.3s ease;
                }
                .btn-start:hover {
                    background-color: var(--accent-teal);
                    color: #000000;
                    box-shadow: 0 0 15px var(--accent-teal);
                }
                .main-layout {
                    display: flex;
                    height: calc(100vh - 60px);
                }
                .sidebar {
                    width: 240px;
                    background-color: var(--panel-bg);
                    border-right: 1px solid var(--border-cyan);
                    padding: 16px;
                }
                .telemetry-item {
                    margin-bottom: 20px;
                }
                .telemetry-value {
                    font-size: 22px;
                    color: var(--accent-teal);
                    font-weight: bold;
                }
                .viewport {
                    flex: 1;
                    padding: 20px;
                }
                .mirror-box {
                    border: 1px solid var(--border-cyan);
                    background-color: #040609;
                    height: 90%;
                    border-radius: 4px;
                    position: relative;
                }
                .mirror-header {
                    display: flex;
                    justify-content: space-between;
                    padding: 12px 16px;
                    border-bottom: 1px solid var(--border-cyan);
                    color: var(--accent-teal);
                    font-weight: bold;
                }
            </style>
        </head>
        <body>
            <div class="top-bar">
                <select id="deviceSelector" class="device-selector" onchange="onDeviceSelected(this.value)">
                    $optionsHtml
                </select>
                <button class="btn-start" onclick="startLaboratory()">INICIAR LABORATORIO</button>
            </div>
            <div class="main-layout">
                <div class="sidebar">
                    <div class="telemetry-item">
                        <div>Velocidad GPS</div>
                        <div class="telemetry-value">0 km/h</div>
                    </div>
                    <div class="telemetry-item">
                        <div>Batería</div>
                        <div class="telemetry-value">0 %</div>
                    </div>
                </div>
                <div class="viewport">
                    <div class="mirror-box">
                        <div class="mirror-header">
                            <span>| ESPEJO REAL ADB</span>
                            <span>Streaming</span>
                        </div>
                    </div>
                </div>
            </div>
            <script>
                function onDeviceSelected(serial) {
                    console.log("Device selected:", serial);
                }
                function startLaboratory() {
                    console.log("Starting laboratory session...");
                }
            </script>
        </body>
        </html>
        """.trimIndent()
    }
}
