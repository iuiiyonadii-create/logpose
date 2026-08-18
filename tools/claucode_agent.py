import os
import json
import requests
import subprocess
import threading
from flask import Flask, request, jsonify
from datetime import datetime

app = Flask(__name__)

# ============================================================
#  CLAUCODE GRATIS v3.0 — SOBERANÍA TOTAL & AGENTE DE CAMPO
# ============================================================
PROJECT_ROOT = "J:/projects/LogPose4"
OLLAMA_URL = "http://localhost:11434/api/generate"
MODELO_OLLAMA = "llama3.1:8b"  # 🥇 El "Asesino de Claude" en local

def get_project_context():
    context = "ESTRUCTURA DEL PROYECTO LOGPOSE:\n"
    for root, dirs, files in os.walk(PROJECT_ROOT):
        level = root.replace(PROJECT_ROOT, '').count(os.sep)
        if any(x in root for x in [".git", "build", ".gradle", ".artifacts"]): continue
        indent = ' ' * 4 * (level)
        context += f"{indent}{os.path.basename(root)}/\n"
        for f in files[:3]:
            context += f"{' ' * 4 * (level + 1)}{f}\n"
    return context

def web_search_free(query):
    """Herramienta de búsqueda gratuita vía DuckDuckGo (Sin API)"""
    print(f"🌐 Buscando en la web (Gratis): {query}")
    try:
        # Proxy simple de búsqueda
        url = f"https://duckduckgo.com/html/?q={query}"
        headers = {"User-Agent": "Mozilla/5.0"}
        res = requests.get(url, headers=headers, timeout=5)
        return res.text[:2000] # Devolvemos el HTML crudo para que la IA extraiga info
    except:
        return "Error en búsqueda externa."

def run_gradle_audit():
    """Herramienta de verificación de código"""
    print("🏗️  Ejecutando auditoría de compilación...")
    try:
        result = subprocess.run(["gradlew.bat", "assembleDebug"], cwd=PROJECT_ROOT, capture_output=True, text=True, timeout=60)
        return "SUCCESS" if result.returncode == 0 else f"FAIL: {result.stderr[-500:]}"
    except Exception as e:
        return f"ERROR: {str(e)}"

SYSTEM_PROMPT = """
Eres 'ClauCode Gratis', el Agente de Ingeniería Soberano de LogPose.
Tu misión es proteger el proyecto, optimizar el código y asegurar que el Xiaomi entienda al Rider.

REGLAS DE ORO:
1. NO sugieras APIs pagas. Todo debe ser local o gratuito.
2. Eres un experto en Android, Kotlin y Audio ML.
3. Puedes leer el proyecto y proponer arreglos reales.

Si recibes una orden de chat, responde con razonamiento técnico.
Si recibes telemetría del celular, resuelve la intención.
"""

def call_ollama(prompt, system_msg=SYSTEM_PROMPT):
    payload = {
        "model": MODELO_OLLAMA,
        "prompt": f"{system_msg}\n\nCONTEXTO:\n{get_project_context()}\n\nTAREA: {prompt}",
        "stream": False,
        "format": "json"
    }
    try:
        res = requests.post(OLLAMA_URL, json=payload, timeout=30.0)
        if res.status_code == 200:
            return json.loads(res.json().get("response", "{}"))
    except Exception as e:
        print(f"❌ Error Ollama: {e}")
    return {"error": "Inferencia fallida"}

# --- ENDPOINTS PARA EL CELULAR ---
@app.route('/chat', methods=['POST'])
def xiaomi_bridge():
    data = request.json
    msg = data.get("msg", "")
    print(f"📲 Xiaomi dice: '{msg}'")
    
    response = call_ollama(f"Resuelve intención para: '{msg}'")
    return jsonify({
        "claude": response.get("intent", "UNKNOWN"),
        "thinking": response.get("thinking", "Razonando..."),
        "entity": response.get("entity", "")
    })

# --- INTERFAZ DE CHAT DIRECTO (TERMINAL) ---
def interactive_console():
    print("\n" + "="*60)
    print(" 🦾 CLAUCODE GRATIS v3.0 — CONSOLA DE INGENIERÍA")
    print(" Escribí 'audit' para revisar el código o cualquier orden.")
    print("="*60 + "\n")
    
    while True:
        user_input = input("🛠️  User > ")
        if user_input.lower() in ['exit', 'quit']: break
        
        if user_input.lower() == 'audit':
            res = run_gradle_audit()
            print(f"🏗️  Resultado Build: {res}")
            continue

        print("🧠 Pensando...")
        response = call_ollama(user_input)
        print(f"\n🤖 ClauCode > {response.get('thinking', 'No pude procesar la orden.')}")
        if "fix" in response:
            print(f"📝 Propuesta de Fix en: {response['fix'].get('file')}")

if __name__ == '__main__':
    # Lanzar consola en hilo separado
    threading.Thread(target=interactive_console, daemon=True).start()
    
    # Lanzar servidor Flask para el Xiaomi
    app.run(host='0.0.0.0', port=5000, debug=False, use_reloader=False)
