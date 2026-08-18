import os
import time
import json
import requests

# ============================================================
#  PROJECT SELF-AUDIT — MONITOR DE FALLOS v1.0
# ============================================================
PROJECT_ROOT = "J:/projects/LogPose4"
FORENSIC_LOG = os.path.join(PROJECT_ROOT, "app/build/outputs/forensic_blackbox.jsonl")
OLLAMA_URL = "http://localhost:11434/api/generate"

def audit_loop():
    print("🕵️  Monitor de Fallos Forenses activado.")
    last_size = 0
    if os.path.exists(FORENSIC_LOG):
        last_size = os.path.getsize(FORENSIC_LOG)

    while True:
        if not os.path.exists(FORENSIC_LOG):
            time.sleep(10)
            continue

        current_size = os.path.getsize(FORENSIC_LOG)
        if current_size > last_size:
            print("🚨 Nuevo fallo detectado en la Caja Negra. Analizando...")
            with open(FORENSIC_LOG, "r", encoding="utf-8") as f:
                f.seek(last_size)
                new_lines = f.readlines()
                for line in new_lines:
                    error_data = json.loads(line)
                    analyze_and_propose_fix(error_data)
            last_size = current_size
        
        time.sleep(5)

def analyze_and_propose_fix(error):
    prompt = f"""
    FALLO DETECTADO:
    Rider dijo: '{error.get('transcripts')}'
    Sherpa entendió: '{error.get('transcripts')}'
    Confianza: {error.get('confidences')}
    Razón de rechazo: {error.get('rationale')}

    TAREA: Propón un cambio en IntentDetector.kt o RioplatenseLinguisticNormalizer.kt para que esto no vuelva a fallar.
    """
    
    payload = {
        "model": "llama3.1:8b",
        "prompt": prompt,
        "stream": False
    }
    
    try:
        res = requests.post(OLLAMA_URL, json=payload, timeout=20)
        if res.status_code == 200:
            fix = res.json().get("response")
            print(f"\n🧠 CLAUCODE PROPONE FIX:\n{fix}\n")
    except:
        pass

if __name__ == "__main__":
    try:
        audit_loop()
    except KeyboardInterrupt:
        print("🛑 Auditoría detenida.")
