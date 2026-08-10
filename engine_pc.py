import os
import sys
import json
import re
import queue
import subprocess

# 1. Auto-instalador de Vosk y Sounddevice para PC
try:
    import vosk
    import sounddevice as sd
except ImportError:
    print("📦 Instalando Vosk y Sounddevice para correr el motor de LogPose en la PC...")
    subprocess.check_call([sys.executable, "-m", "pip", "install", "vosk", "sounddevice", "numpy"])
    import vosk
    import sounddevice as sd

# 2. Rutas a los Assets reales del proyecto LogPose
MODEL_PATH = r"C:\projects\LogPose4\app\src\main\assets\model-es"
GLOSARIO_PATH = r"C:\projects\LogPose4\app\src\main\assets\logpose_glosario.json"

if not os.path.exists(MODEL_PATH):
    MODEL_PATH = r"app\src\main\assets\model-es"

if not os.path.exists(GLOSARIO_PATH):
    GLOSARIO_PATH = r"app\src\main\assets\logpose_glosario.json"

# Cargar Glosario Rioplatense
glosario = {}
if os.path.exists(GLOSARIO_PATH):
    with open(GLOSARIO_PATH, "r", encoding="utf-8") as f:
        glosario = json.load(f)

# 3. Motor Fonético THAMIS Rioplatense (ALF-R Core)
def thamis_normalize(text):
    text = text.lower().strip()
    text = re.sub(r'[áàäâ]', 'a', text)
    text = re.sub(r'[éèëê]', 'e', text)
    text = re.sub(r'[íìïî]', 'i', text)
    text = re.sub(r'[óòöô]', 'o', text)
    text = re.sub(r'[úùüû]', 'u', text)
    
    # Correcciones de alucinaciones acústicas conocidas de Vosk
    common_fixes = {
        "poneduki": "pone duki",
        "poneuki": "pone duki",
        "duque": "duki",
        "duty": "duki",
        "duck y": "duki",
        "duck": "duki",
        "ponen lucky": "pone duki",
        "pone lucky": "pone duki",
        "to kill": "duki",
        "hornee tonky": "pone duki",
        "con educ y": "pone duki",
        "droga lo ponen lucky": "pone duki",
        "ponencia": "pone ysy a",
        "pone y cia": "pone ysy a",
        "y cia": "ysy a",
        "ysya": "ysy a",
        "isía": "ysy a",
        "isi a": "ysy a",
        "easy a": "ysy a",
        "milo j": "milo j",
        "miloj": "milo j"
    }
    
    for bad, good in common_fixes.items():
        if bad in text:
            text = text.replace(bad, good)

    return text

def process_thamis_pipeline(raw_text):
    clean = thamis_normalize(raw_text)
    
    # MÚSICA
    music_verbs = ["pone", "poné", "poneme", "reproduce", "reproduci", "reproducir", "play", "escuchar"]
    for verb in music_verbs:
        if clean.startswith(verb):
            query = clean[len(verb):].strip()
            # Desprender prefijos o stopwords
            query = re.sub(r'^(de|en|el|la|los|las|un|una)\s+', '', query)
            query = re.sub(r'\s+en\s+(youtube music|yt music|youtube|spotify)$', '', query)
            if not query or len(query) < 3 or query in ["po", "lleva", "ponemos"]:
                return "PLAY_MUSIC", "Reanudar música actual", ""
            return "PLAY_MUSIC", f"PlayMusic(query='{query}') -> Spotify/YouTube Music", query

    # NAVEGACIÓN
    nav_triggers = ["llevame", "lleva", "guiame", "navegar", "ir a", "anda a", "camino a"]
    for trigger in nav_triggers:
        if clean.startswith(trigger) or f" {trigger} " in f" {clean} ":
            dest = re.sub(r'.*?(' + trigger + r')\s*(a|al|hacia)?\s*', '', clean).strip()
            return "NAVIGATE", f"Navigate(destination='{dest}') -> Google Maps", dest

    # TELEFONÍA
    if any(w in clean for w in ["silenciar", "silencio", "ignorar", "mute", "shh"]):
        return "SILENCE_CALL", "SilenceCall() -> Silenciar ringer", ""
    if any(w in clean for w in ["atender", "aceptar"]):
        return "ANSWER_CALL", "AcceptCall() -> Atender llamada", ""
    if any(w in clean for w in ["rechazar", "cortar", "colgar"]):
        return "REJECT_CALL", "RejectCall() -> Colgar llamada", ""
    if clean.startswith("llama") or clean.startswith("llamar"):
        contact = re.sub(r'^(llama|llamar|llamale)\s+(a)?\s*', '', clean).strip()
        return "CALL_CONTACT", f"Call(contact='{contact}') -> Marcar número", contact

    # CLIMA
    if any(w in clean for w in ["clima", "pronostico", "llover", "lluvia", "temperatura", "tiempo"]):
        return "GET_WEATHER", "GetWeather() -> Reporte vocal de clima", ""

    # APPS
    if clean.startswith("abrir") or clean.startswith("abre") or clean.startswith("abri"):
        app = re.sub(r'^(abrir|abre|abri)\s*', '', clean).strip()
        return "OPEN_APP", f"OpenApp(app='{app}')", app

    return "UNKNOWN", "Comando no reconocido", clean

# 4. Captura de Audio de Micrófono en Tiempo Real
q = queue.Queue()

def audio_callback(indata, frames, time_info, status):
    if status:
        print(status, file=sys.stderr)
    q.put(bytes(indata))

def main():
    print("======================================================================")
    print(" 🧠 MOTOR VOSK OFFLINE REAL DE LOGPOSE / THAMIS (CORRIENDO EN PC)")
    print("======================================================================")
    print(f"📁 Cargando Modelo Vosk desde: {MODEL_PATH}")
    
    vosk.SetLogLevel(-1)
    model = vosk.Model(MODEL_PATH)
    rec = vosk.KaldiRecognizer(model, 16000)

    print("✅ Modelo Vosk cargado con éxito.")
    print("🎙️ Micrófono de PC escuchando en tiempo real... (Presioná CTRL+C para salir)\n")

    with sd.RawInputStream(samplerate=16000, blocksize=8000, dtype='int16', channels=1, callback=audio_callback):
        while True:
            data = q.get()
            if rec.AcceptWaveform(data):
                res = json.loads(rec.Result())
                raw_text = res.get("text", "").strip()
                
                if raw_text:
                    intent, action, payload = process_thamis_pipeline(raw_text)
                    
                    print(f"🗣️ VOSK RAW        : \"{raw_text}\"")
                    print(f"✨ THAMIS MATCH    : \"{thamis_normalize(raw_text)}\"")
                    print(f"🎯 INTENCIÓN (IA)  : {intent}")
                    print(f"🚀 ACCIÓN DISPATCH : {action}")
                    print("-" * 70 + "\n")

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\nMotor de PC detenido.")
        sys.exit(0)
