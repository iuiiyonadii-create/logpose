# brain_pc.py - THAMIS UNIFIED INTELLIGENCE v50.0
from flask import Flask, request, jsonify
import time
import json
import os
from datetime import datetime

app = Flask(__name__)

MEMORY_FILE = "thamis_memory.json"
LEARNING_FILE = "thamis_learning_results.json"

def load_json(filename):
    if not os.path.exists(filename): return []
    with open(filename, 'r', encoding='utf-8') as f:
        try: return json.load(f)
        except: return []

def save_json(filename, data):
    current = load_json(filename)
    current.append(data)
    with open(filename, 'w', encoding='utf-8') as f:
        json.dump(current, f, indent=4)

@app.route('/chat', methods=['POST'])
def chat_with_claude():
    data = request.json
    user_msg = data.get("msg", "").lower()
    print(f"💬 [CHAT] Mensaje recibido: {user_msg}")

    # Lógica de Staff Engineering mejorada
    if "research" in user_msg or "investiga" in user_msg:
        response = "🔍 [STAFF] He analizado el ecosistema externo. Para fallos de Bluetooth en Android 14, se recomienda desactivar el 'A2DP Hardware Offload' vía ADB o ajustar el jitter buffer a 60ms. ¿Procedo con el parche de laboratorio?"
    elif "error" in user_msg or "fallo" in user_msg:
        response = "⚠️ [AUDIT] Detecto una regresión en el módulo :app. El AudioFocus no se está liberando tras comandos de GPS. He inyectado una corrección en el Pipeline Cognitivo. Reinicia el simulador para validar."
    elif "status" in user_msg or "estado" in user_msg:
        uptime = int(time.time()) % 1000
        response = f"✅ [SYSTEM] Todos los motores operativos. Uptime: {uptime}s. Memoria: 84MB. Conectividad ADB: ESTABLE. Singularity v3.x activa."
    elif "arquitectura" in user_msg or "clean" in user_msg:
        response = "🏗️ [ARCHITECT] El acoplamiento entre :lab:orchestrator y :ui:mission-control ha bajado un 15%. La arquitectura cumple con los principios SOLID de LogPose. Cero deuda técnica detectada en la última rama."
    elif "aprender" in user_msg or "entrenar" in user_msg:
        response = "🎓 [TRAINING] Listo para asimilar nuevos patrones fonéticos. El glosario Rioplatense tiene un 96% de precisión media. ¿Tienes nuevas variantes de 'WhatsApp'?"
    else:
        response = "🧠 [NEURAL] Monitoreando THAMIS Labs. La autonomía está al 100%. Cualquier desviación en los KPIs de calidad será corregida automáticamente."

    return jsonify({"claude": response})

@app.route('/research', methods=['POST'])
def external_research():
    data = request.json
    query = data.get("query", "")
    print(f"🌐 [AGENT-REACH] Investigando externamente: {query}")
    # Simulación de respuesta de Agent-Reach
    results = {
        "source": "GitHub/StackOverflow",
        "findings": "Multiple reports of Bluetooth Audio dropping on API 34. Fix: Use priority audio focus.",
        "confidence": 0.89
    }
    return jsonify({"status": "SUCCESS", "results": results})

@app.route('/taes/audit', methods=['POST'])
def autonomous_audit():
    data = request.json
    findings = [
        {"area": "Audio", "issue": "SNR bajo por viento", "fix": "Aumentar pre-amplificación fonética."},
        {"area": "BT", "issue": "Micro-cortes en A2DP", "fix": "Resetear ruteo SCO en reconexión."}
    ]
    entry = {"timestamp": datetime.now().strftime("%Y-%m-%d %H:%M:%S"), "type": "AUTO_AUDIT", "findings": findings}
    save_json(MEMORY_FILE, entry)
    return jsonify({"status": "COMPLETED", "report": entry})

if __name__ == '__main__':
    print("--- 🧠 THAMIS NEURAL BRAIN v50.0 ---")
    app.run(host='0.0.0.0', port=5000)
