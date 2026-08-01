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

    # Lógica de respuesta inteligente de Claude (Simulada para Staff Engineering)
    if "error" in user_msg or "fallo" in user_msg:
        response = "He analizado el Neural Feed. Detecto una degradación en el canal SCO. ¿Quieres que inyecte una corrección de buffer en el S8?"
    elif "status" in user_msg or "estado" in user_msg:
        response = "Sistemas nominales. CPU al 12%. LogPose v0.8.2 reportando estabilidad del 94%."
    elif "aprender" in user_msg or "entrenar" in user_msg:
        response = "Listo para nuevo entrenamiento. ¿Es sobre un comando de Spotify o navegación?"
    else:
        response = "Entendido. Estoy monitoreando el laboratorio. Cualquier anomalía en el hardware será reportada inmediatamente en este feed."

    return jsonify({"claude": response})

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
