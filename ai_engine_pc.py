import sys
import subprocess
import os

# Auto-instalar Faster-Whisper y Sounddevice para PC
try:
    from faster_whisper import WhisperModel
    import sounddevice as sd
    import numpy as np
except ImportError:
    print("📦 Instalando Motor Neural Whisper AI para la PC (OpenAI Speech AI)...")
    subprocess.check_call([sys.executable, "-m", "pip", "install", "faster-whisper", "sounddevice", "numpy"])
    from faster_whisper import WhisperModel
    import sounddevice as sd
    import numpy as np

import json
import re

GLOSARIO_PATH = r"C:\projects\LogPose4\app\src\main\assets\logpose_glosario.json"
if not os.path.exists(GLOSARIO_PATH):
    GLOSARIO_PATH = r"app\src\main\assets\logpose_glosario.json"

glosario = {}
if os.path.exists(GLOSARIO_PATH):
    with open(GLOSARIO_PATH, "r", encoding="utf-8") as f:
        glosario = json.load(f)

def clean_spanish(text):
    text = text.lower().strip()
    text = re.sub(r'[áàäâ]', 'a', text)
    text = re.sub(r'[éèëê]', 'e', text)
    text = re.sub(r'[íìïî]', 'i', text)
    text = re.sub(r'[óòöô]', 'o', text)
    text = re.sub(r'[úùüû]', 'u', text)
    text = re.sub(r'[^\w\s]', '', text)
    return text

def parse_thamis_intent(raw_text):
    clean = clean_spanish(raw_text)
    
    # 1. MÚSICA
    music_verbs = ["pone", "pone", "poneme", "reproduce", "reproduci", "reproducir", "play", "escuchar"]
    for verb in music_verbs:
        if clean.startswith(verb):
            query = clean[len(verb):].strip()
            query = re.sub(r'^(de|en|el|la|los|las|un|una)\s+', '', query)
            query = re.sub(r'\s+en\s+(youtube music|yt music|youtube|spotify)$', '', query)
            if not query or len(query) < 3 or query in ["po", "lleva", "ponemos"]:
                return "PLAY_MUSIC", "Reanudar música actual", ""
            return "PLAY_MUSIC", f"PlayMusic(query='{query}') -> Spotify/YouTube Music", query

    # 2. NAVEGACIÓN
    nav_triggers = ["llevame", "lleva", "guiame", "navegar", "ir a", "anda a", "camino a"]
    for trigger in nav_triggers:
        if clean.startswith(trigger) or f" {trigger} " in f" {clean} ":
            dest = re.sub(r'.*?(' + trigger + r')\s*(a|al|hacia)?\s*', '', clean).strip()
            return "NAVIGATE", f"Navigate(destination='{dest}') -> Google Maps", dest

    # 3. TELEFONÍA
    if any(w in clean for w in ["silenciar", "silencio", "ignorar", "mute", "shh"]):
        return "SILENCE_CALL", "SilenceCall() -> Silenciar ringer", ""
    if any(w in clean for w in ["atender", "aceptar"]):
        return "ANSWER_CALL", "AcceptCall() -> Atender llamada", ""
    if any(w in clean for w in ["rechazar", "cortar", "colgar"]):
        return "REJECT_CALL", "RejectCall() -> Colgar llamada", ""
    if clean.startswith("llama") or clean.startswith("llamar"):
        contact = re.sub(r'^(llama|llamar|llamale)\s+(a)?\s*', '', clean).strip()
        return "CALL_CONTACT", f"Call(contact='{contact}') -> Marcar número", contact

    # 4. CLIMA
    if any(w in clean for w in ["clima", "pronostico", "llover", "lluvia", "temperatura", "tiempo"]):
        return "GET_WEATHER", "GetWeather() -> Reporte vocal de clima", ""

    # 5. APPS
    if clean.startswith("abrir") or clean.startswith("abre") or clean.startswith("abri"):
        app = re.sub(r'^(abrir|abre|abri)\s*', '', clean).strip()
        return "OPEN_APP", f"OpenApp(app='{app}')", app

    return "UNKNOWN", "Comando no reconocido", clean

def main():
    print("======================================================================")
    print(" 🧠 MOTOR NEURAL WHISPER AI (OPENAI NEURAL SPEECH ENGINE EN PC)")
    print("======================================================================")
    print("📁 Cargando Modelo Neural Whisper (tiny.en/es)...")
    
    model = WhisperModel("tiny", device="cpu", compute_type="int8")
    print("✅ Modelo Neural Whisper AI listo. ¡NO ADIVINA, ESCUCHA REAL!")
    print("🎙️ Micrófono de PC escuchando... (Hablá una frase y hacé una pausa corta)\n")

    sample_rate = 16000
    
    while True:
        try:
            print("🟢 Escuchando audio del micrófono (3 segundos de captura)...")
            recording = sd.rec(int(3 * sample_rate), samplerate=sample_rate, channels=1, dtype='float32')
            sd.wait()
            
            audio_data = recording.flatten()
            
            if np.max(np.abs(audio_data)) < 0.02:
                continue

            segments, info = model.transcribe(audio_data, language="es", beam_size=5)
            text = " ".join([seg.text for seg in segments]).strip()

            if text:
                intent, action, payload = parse_thamis_intent(text)
                print("\n" + "=" * 70)
                print(f"🧠 WHISPER AI RECOGNIZED : \"{text}\"")
                print(f"✨ TEXTO NORMALIZADO    : \"{clean_spanish(text)}\"")
                print(f"🎯 INTENCIÓN CLASIFICADA: {intent}")
                print(f"🚀 ACCIÓN TEÓRICA       : {action}")
                print("=" * 70 + "\n")

        except KeyboardInterrupt:
            print("\nMotor Neural detenido.")
            sys.exit(0)
        except Exception as e:
            print(f"⚠️ Excepción: {e}")

if __name__ == "__main__":
    main()
