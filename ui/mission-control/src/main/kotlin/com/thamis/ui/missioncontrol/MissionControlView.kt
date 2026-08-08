package com.thamis.ui.missioncontrol

import com.thamis.lab.performance.adb.AdbManager
import com.thamis.lab.performance.device.DeviceInfo
import java.util.Locale

/**
 * THAMIS LAB MISSION CONTROL v3.0 - Professional HTML/JS UI
 * Encargado de renderizar la interfaz de Singularity v3.x.
 */
public object MissionControlView {

    public fun generateDashboardHtml(devices: List<DeviceInfo>, selectedSerial: String?): String {
        val deviceListHtml = if (devices.isEmpty()) {
            """<div class="empty-state">No se detectaron dispositivos vinculados.</div>"""
        } else {
            devices.joinToString("\n") { dev ->
                val activeClass = if (dev.deviceId == selectedSerial) "active-card" else ""
                val statusText = if (dev.deviceId == selectedSerial) "ACTIVO" else "CONECTADO"
                """
                <div class="device-card $activeClass">
                    <div class="device-info">
                        <div class="device-name">${dev.displayName} (API ${dev.apiLevel})</div>
                        <div class="device-serial">Serial: ${dev.deviceId} | Type: PHYSICAL_ANDROID</div>
                    </div>
                    <div class="device-actions">
                        <button class="btn-select" onclick="selectDevice('${dev.deviceId}')">Seleccionar</button>
                        <span class="status-badge"><i class="fas fa-battery-three-quarters"></i> 90%</span>
                        <span class="os-version">API ${dev.apiLevel}</span>
                        <span class="active-indicator" style="display: ${if (dev.deviceId == selectedSerial) "inline" else "none"}">$statusText</span>
                    </div>
                </div>
                """.trimIndent()
            }
        }

        return """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <title>THAMIS LAB — MISSION CONTROL (Singularity v3.x)</title>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
            <style>
                :root {
                    --bg-dark: #05080a;
                    --sidebar-bg: #0a1118;
                    --panel-bg: #0d131a;
                    --card-bg: #111a24;
                    --accent-cyan: #00f2ff;
                    --accent-blue: #008891;
                    --text-main: #e0f7fc;
                    --text-dim: #8ba5ac;
                    --danger-red: #ff4c4c;
                    --success-green: #3DDC97;
                }
                body {
                    margin: 0; padding: 0;
                    background-color: var(--bg-dark);
                    color: var(--text-main);
                    font-family: 'Consolas', 'Segoe UI', monospace;
                    display: flex; height: 100vh;
                    overflow: hidden;
                }
                /* SIDEBAR */
                .sidebar {
                    width: 260px;
                    background-color: var(--sidebar-bg);
                    border-right: 1px solid rgba(0, 242, 255, 0.1);
                    display: flex; flex-direction: column;
                    padding: 20px 0;
                    flex-shrink: 0;
                }
                .brand {
                    padding: 0 24px 30px;
                    font-size: 20px; font-weight: bold; color: var(--accent-cyan);
                    display: flex; align-items: center; gap: 10px;
                }
                .brand small { font-size: 10px; color: var(--text-dim); display: block; }
                .nav-group { overflow-y: auto; flex: 1; }
                .nav-item {
                    padding: 12px 24px;
                    display: flex; align-items: center; gap: 12px;
                    cursor: pointer; transition: all 0.2s;
                    color: var(--text-dim); border-left: 3px solid transparent;
                    font-size: 13px;
                }
                .nav-item:hover { background: rgba(0, 242, 255, 0.05); color: #fff; }
                .nav-item.active { 
                    color: var(--accent-cyan); 
                    background: rgba(0, 242, 255, 0.1);
                    border-left-color: var(--accent-cyan);
                }
                .nav-item i { width: 20px; text-align: center; font-size: 14px; }

                /* MAIN CONTENT */
                .main-container { flex: 1; display: flex; flex-direction: row; position: relative; }
                .main-view { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
                .view-panel { display: none; flex: 1; padding: 30px; overflow-y: auto; }
                .view-panel.active { display: flex; flex-direction: column; }

                .panel-header {
                    display: flex; justify-content: space-between; align-items: center;
                    margin-bottom: 25px;
                }
                .panel-header h2 { margin: 0; font-size: 24px; color: #fff; }
                .btn-refresh {
                    background: var(--accent-blue); color: #fff; border: none;
                    padding: 8px 16px; border-radius: 4px; font-weight: bold; cursor: pointer;
                    display: flex; align-items: center; gap: 8px;
                }

                /* DEVICE LIST */
                .device-list { display: flex; flex-direction: column; gap: 15px; }
                .device-card {
                    background: var(--card-bg); border: 1px solid rgba(0, 242, 255, 0.1);
                    border-radius: 8px; padding: 20px;
                    display: flex; justify-content: space-between; align-items: center;
                    transition: border 0.3s;
                }
                .device-card.active-card { border-color: var(--accent-cyan); box-shadow: 0 0 15px rgba(0, 242, 255, 0.1); }
                .device-name { font-size: 16px; font-weight: bold; color: #fff; margin-bottom: 4px; }
                .device-serial { font-size: 12px; color: var(--text-dim); }
                .device-actions { display: flex; align-items: center; gap: 20px; }
                .status-badge { color: var(--accent-cyan); font-weight: bold; display: flex; align-items: center; gap: 5px; }
                .active-indicator { color: var(--success-green); font-weight: bold; font-size: 12px; }
                .btn-select { 
                    background: #2d3e4a; color: #fff; border: none; padding: 6px 15px; border-radius: 4px;
                    font-size: 13px; cursor: pointer;
                }

                /* SIMULATOR GRID */
                .chaos-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; }
                .chaos-card {
                    background: var(--card-bg); border: 1px solid rgba(255, 255, 255, 0.05);
                    padding: 25px; border-radius: 8px; text-align: center;
                    cursor: pointer; transition: transform 0.2s;
                }
                .chaos-card:hover { transform: translateY(-3px); border-color: var(--accent-cyan); }
                .chaos-card i { font-size: 30px; margin-bottom: 15px; display: block; }
                .chaos-card.danger i { color: var(--danger-red); }
                .chaos-card.info i { color: var(--accent-cyan); }
                .chaos-card.research { border-color: var(--accent-cyan); background: rgba(0, 242, 255, 0.05); }

                /* CHAT (INTELLIGENCE) */
                .chat-box {
                    flex: 1; background: #080c11; border: 1px solid rgba(0, 242, 255, 0.1);
                    border-radius: 8px; display: flex; flex-direction: column; max-width: 900px;
                }
                .chat-history { flex: 1; padding: 20px; overflow-y: auto; font-size: 13px; line-height: 1.6; }
                .msg { margin-bottom: 15px; border-bottom: 1px solid rgba(255, 255, 255, 0.02); padding-bottom: 10px; }
                .msg.bot { color: var(--accent-cyan); }
                .msg.user { color: #fff; }
                .input-row { padding: 15px; display: flex; gap: 10px; }
                .input-row input { 
                    flex: 1; background: #0a1118; border: 1px solid #1a252b; color: #fff;
                    padding: 12px; border-radius: 4px; outline: none;
                }

                /* RIGHT SIDEBAR (OBJECTIVOS) */
                .right-bar {
                    width: 300px; background: #0a1118; border-left: 1px solid rgba(0, 242, 255, 0.1);
                    padding: 20px; display: flex; flex-direction: column; gap: 20px;
                }
                .goal-section { background: var(--card-bg); padding: 15px; border-radius: 6px; }
                .goal-section h3 { margin: 0 0 15px; font-size: 12px; color: var(--accent-cyan); text-transform: uppercase; }
                .goal-item { margin-bottom: 20px; }
                .goal-title { font-size: 13px; font-weight: bold; margin-bottom: 8px; }
                .progress-bg { background: #080c11; height: 6px; border-radius: 3px; position: relative; }
                .progress-fill { height: 100%; border-radius: 3px; background: var(--accent-cyan); }
                .goal-meta { display: flex; justify-content: space-between; font-size: 10px; margin-top: 5px; color: var(--text-dim); }

                /* BOTTOM BAR */
                .bottom-bar {
                    position: fixed; bottom: 0; left: 0; right: 0; height: 30px;
                    background: #030508; border-top: 1px solid rgba(0, 242, 255, 0.1);
                    display: flex; align-items: center; padding: 0 15px; font-size: 10px; gap: 20px;
                }
                .status-ok { color: var(--success-green); }
            </style>
        </head>
        <body>
            <div class="sidebar">
                <div class="brand">
                    <i class="fas fa-bolt"></i>
                    <div>THAMIS LAB <small>Engineering Platform v2.5</small></div>
                </div>
                <div class="nav-group">
                    <div class="nav-item" onclick="showView(0)"><i class="fas fa-rocket"></i> Misión (OS)</div>
                    <div class="nav-item" onclick="showView(1)"><i class="fas fa-brain"></i> Entrenamiento (CTC)</div>
                    <div class="nav-item" onclick="showView(2)"><i class="fas fa-bullseye"></i> Inteligencia (PIE)</div>
                    <div class="nav-item" onclick="showView(3)"><i class="fas fa-list-check"></i> Sprints</div>
                    <div class="nav-item" onclick="showView(4)"><i class="fas fa-car"></i> Simulador</div>
                    <div class="nav-item" onclick="showView(5)"><i class="fas fa-home"></i> Panel Inicio</div>
                    <div class="nav-item active" onclick="showView(6)"><i class="fas fa-mobile-alt"></i> Dispositivos</div>
                    <div class="nav-item" onclick="showView(7)"><i class="fas fa-flask"></i> Laboratorios</div>
                    <div class="nav-item" onclick="showView(8)"><i class="fas fa-box"></i> LogPose</div>
                    <div class="nav-item" onclick="showView(9)"><i class="fas fa-robot"></i> Inteligencia</div>
                    <div class="nav-item" onclick="showView(10)"><i class="fas fa-book"></i> Conocimiento</div>
                    <div class="nav-item" onclick="showView(11)"><i class="fas fa-chart-bar"></i> Analíticas</div>
                    <div class="nav-item" onclick="showView(12)"><i class="fas fa-microchip"></i> Sistema</div>
                </div>
            </div>

            <div class="main-container">
                <div class="main-view">
                    <!-- VIEW 6: DISPOSITIVOS -->
                    <div id="view-6" class="view-panel active">
                        <div class="panel-header">
                            <h2>Panel de Control de Dispositivos</h2>
                            <button class="btn-refresh"><i class="fas fa-sync"></i> Re-escanear Ahora</button>
                        </div>
                        <div class="device-list">
                            $deviceListHtml
                        </div>
                    </div>

                    <!-- VIEW 4: SIMULADOR -->
                    <div id="view-4" class="view-panel">
                        <div class="panel-header">
                            <h2>Motor de Simulación de Entorno</h2>
                        </div>
                        <div class="chaos-grid">
                            <div class="chaos-card danger" onclick="logAction('SIMULAR TORMENTA')">
                                <i class="fas fa-bolt"></i> Simular Tormenta (Ruido + GPS Lag)
                            </div>
                            <div class="chaos-card info" onclick="logAction('SIMULAR CARRETERA')">
                                <i class="fas fa-road"></i> Modo Carretera (140 km/h)
                            </div>
                            <div class="chaos-card danger" onclick="logAction('INYECTAR ERROR SCO')">
                                <i class="fas fa-headset"></i> Inyectar Fallo Bluetooth SCO
                            </div>
                            <div class="chaos-card research" onclick="startResearch()">
                                <i class="fas fa-search-plus"></i> INVESTIGACIÓN AGENT-REACH
                            </div>
                        </div>
                    </div>

                    <!-- VIEW 9: CHAT INTELIGENCIA -->
                    <div id="view-9" class="view-panel">
                        <div class="panel-header">
                            <h2>Asistente Arquitecto Residente</h2>
                        </div>
                        <div class="chat-box">
                            <div class="chat-history" id="chatHistory">
                                <div class="msg bot">[THAMIS]: Sistema operativo de IA listo. Esperando entrada...</div>
                            </div>
                            <div class="input-row">
                                <input type="text" id="chatInput" placeholder="Escribe un comando o consulta técnica..." onkeydown="if(event.key==='Enter') sendMessage()">
                                <button class="btn-refresh" onclick="sendMessage()">ENVIAR</button>
                            </div>
                        </div>
                    </div>

                    <!-- PLACEHOLDERS FOR OTHERS -->
                    <div id="view-11" class="view-panel">
                        <h2>Analíticas de Rendimiento</h2>
                        <p>Telemetría en tiempo real desde hardware vinculado...</p>
                    </div>
                </div>

                <!-- RIGHT SIDEBAR: OBJETIVOS -->
                <div class="right-bar">
                    <div class="brand" style="padding: 0; font-size: 16px;">
                        <i class="fas fa-crosshairs"></i> OBJETIVOS
                    </div>
                    
                    <div class="goal-section">
                        <h3>Diario</h3>
                        <div class="goal-item">
                            <div class="goal-title">Validar 50 comandos nuevos</div>
                            <div class="progress-bg"><div class="progress-fill" style="width: 0%;"></div></div>
                            <div class="goal-meta"><span>0%</span> <span>PENDIENTE</span></div>
                        </div>
                    </div>

                    <div class="goal-section">
                        <h3>Principal</h3>
                        <div class="goal-item">
                            <div class="goal-title">Evolución Autónoma v5.0</div>
                            <div class="progress-bg"><div class="progress-fill" style="width: 78%; background: var(--success-green);"></div></div>
                            <div class="goal-meta"><span>78%</span> <span>EN_PROGRESO</span></div>
                        </div>
                    </div>

                    <div class="goal-section">
                        <h3>Sprint</h3>
                        <div class="goal-item">
                            <div class="goal-title">Comprensión WhatsApp</div>
                            <div class="progress-bg"><div class="progress-fill" style="width: 96%; background: var(--success-green);"></div></div>
                            <div class="goal-meta"><span>96%</span> <span>EN_PROGRESO</span></div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="bottom-bar">
                <span class="status-ok">● SISTEMA OPERATIVO</span>
                <span>DB: Connected (SQLite WAL)</span>
                <span>Tasks: 0 Active</span>
                <span>Device: ${selectedSerial ?: "Ninguno"}</span>
                <span style="margin-left: auto;">${java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}</span>
            </div>

            <script>
                const views = document.querySelectorAll('.view-panel');
                const navItems = document.querySelectorAll('.nav-item');

                function showView(index) {
                    views.forEach(v => v.classList.remove('active'));
                    navItems.forEach((n, i) => i === index ? n.classList.add('active') : n.classList.remove('active'));
                    
                    const target = document.getElementById('view-' + index);
                    if (target) {
                        target.classList.add('active');
                    } else {
                        alert("Sección " + index + " aún no implementada en v3.0");
                    }
                }

                function addMsg(text, type) {
                    const history = document.getElementById('chatHistory');
                    const div = document.createElement('div');
                    div.className = 'msg ' + type;
                    div.innerHTML = (type === 'bot' ? 'Local Offline Engine: ' : 'You: ') + text;
                    history.appendChild(div);
                    history.scrollTop = history.scrollHeight;
                }

                function sendMessage() {
                    const input = document.getElementById('chatInput');
                    const text = input.value.trim();
                    if (!text) return;
                    addMsg(text, 'user');
                    input.value = '';
                    // ... (rest of bot logic)
                }

                function startResearch() {
                    const topic = prompt("Tema de investigación Agent-Reach:");
                    if (topic) {
                        addMsg("Iniciando misión de investigación externa sobre: " + topic, 'user');
                        addMsg("🔍 [AGENT-REACH] Consultando fuentes Staff en internet...", 'bot');
                    }
                }

                function selectDevice(serial) {
                    alert("Cambiando objetivo a dispositivo: " + serial);
                    // Dispara evento al backend
                }
                
                function logAction(msg) {
                    console.log("Action triggered: " + msg);
                    alert("Ejecutando en laboratorio: " + msg);
                }
            </script>
        </body>
        </html>
        """.trimIndent()
    }
}
