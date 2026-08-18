import asyncio
import sys
import json
import numpy as np
import re
import os

try:
    import sounddevice as sd
    from vosk import Model, KaldiRecognizer
except ImportError:
    print("❌ Faltan dependencias. Ejecutá: pip install sounddevice vosk numpy")
    sys.exit(1)

# Configuración Atómica
SAMPLE_RATE = 16000
CHANNELS = 1
CHUNK_SIZE = 1024
PATH_MODELO_VOSK = "C:\\projects\\LogPose4\\app\\src\\main\\assets\\model-es"
PATH_GLOSARIO = "C:\\projects\\LogPose4\\app\\src\\main\\assets\\logpose_glosario.json"

# ============================================================
#  🇦🇷 CORE ARGENTUM ULC v4.4 — ACTION BIGRAMS & STREET FORCING
# ============================================================
CONSTITUCION_ARGENTUM = {
    "RAICES_DISPARADORES": {
        "SEND_MESSAGE": ["decile", "decime", "decidme", "escribile", "contestale", "mandar"],
        # Fix v4.4: Límites bilaterales estrictos para Volumen
        "SET_VOLUME": [r"\bsubi\w*\b", r"\bbaja\w*\b", r"\bvolumen\b", r"\bmut\w*\b", r"\bsilenc\w*\b"],
        "NAVIGATE": ["llevame", "andá", "anda", "encará", "encara", "rumbear", "rumbea", "ruta", "guiame", "llegar", "viajar", "vamos"],
        "OPEN_APP": ["abrí", "abrir", "abrite", "lanzar", "metete"],
        "PLAY_MUSIC": ["pon", "repro", "largame", "mandale", "tirá", "play", "pasame", "escuchar"]
    },
    "MAPEO_FONETICO": {
        "bonete": "pone", "ponente": "pone", "bonet": "pone", "ponet": "pone",
        "bowl": "dembowholic turreo edit", "recato": "roncan rkt", "que a": "khea", "ikea": "khea"
    }
}

# Geografía Crítica para Street Forcing
STREETS_DATABASE = {"corrientes", "rivadavia", "obelisco", "callao", "general paz", "libertador", "cabildo", "santa fe", "9 de julio"}

# Lista de descarte de "Ciencia Ficción" y charla casual
WASTE_CASUAL_TALK = {"meteoritos", "inter", "planetarios", "catastrofica", "argumento", "entusiasmo", "cual", "seria", "mejor"}

class ArgentumStaffEngineV44:
    def __init__(self):
        self.universo_validado = set()
        self.cargar_glosario()

    def cargar_glosario(self):
        if os.path.exists(PATH_GLOSARIO):
            with open(PATH_GLOSARIO, 'r', encoding='utf-8') as f:
                data = json.load(f)
                for artist in data.get("musica", {}).get("artistas", []): self.universo_validado.add(artist.lower())
                for song in data.get("musica", {}).get("canciones", []): self.universo_validado.add(song.lower())
                for street in data.get("navegacion", {}).get("arterias", []): self.universo_validado.add(street.lower())
                for poi in data.get("navegacion", {}).get("destinos_comunes", []): self.universo_validado.add(poi.lower())

    def resolver_contrato(self, frase_usuario: str) -> dict:
        raw_input = frase_usuario.lower().strip()
        
        # 1. Pop Noise Stripper
        tokens_raw = raw_input.split()
        if tokens_raw and tokens_raw[0] in ["log", "lujo", "lodge", "los", "lock"]:
            raw_input = " ".join(tokens_raw[1:]).strip()

        # 2. Street Forcing & Entity Isolation (Fix Callao/Meteoritos)
        detected_street = None
        for street in STREETS_DATABASE:
            if street in raw_input:
                detected_street = street
                break
        
        if detected_street:
            print(f"📍 Street Forcing: '{detected_street}' isolated. Purging waste.")
            return {"intent": "NAVIGATE", "entity": detected_street, "original": frase_usuario}

        # 3. Bigram Validation & Intent Detection
        intent_detectado = "UNKNOWN"
        token_trigger = ""
        
        # Pre-procesamiento de verbos ocluidos
        texto_proc = raw_input.replace("bonete", "pone").replace("ponente", "pone")
        words = texto_proc.split()

        for intent, patterns in CONSTITUCION_ARGENTUM["RAICES_DISPARADORES"].items():
            for p in patterns:
                for w in words:
                    # Match por regex o startswith
                    if (p.startswith("\\") and re.search(p, w)) or (not p.startswith("\\") and w.startswith(p)):
                        # Fix 'Decir': No es comando
                        if p in ["dec", "dic"] and w == "decir": continue
                        # Fix 'Llevar'/'Pasar' Infinitivos (Only specific rioplatense allowed)
                        if intent == "NAVIGATE" and w == "llevar": continue
                        if intent == "PLAY_MUSIC" and w in ["pasar", "pasaria", "pasara"]: continue
                        
                        intent_detectado = intent
                        token_trigger = w
                        break
                if intent_detectado != "UNKNOWN": break
            if intent_detectado != "UNKNOWN": break

        # 4. Slot Filling v4.4
        payload_final = "none"
        if intent_detectado != "UNKNOWN":
            try:
                idx = words.index(token_trigger)
                right_side = words[idx + 1:]
                
                # Purga de muletillas y "Meteoritos"
                muletillas = {"en", "entonces", "ahora", "a", "al", "la", "el", "de", "con", "y", "que", "un", "una"}
                final_tokens = [t for t in right_side if t not in muletillas and t not in WASTE_CASUAL_TALK]
                
                payload_final = " ".join(final_tokens).strip()
                if not payload_final: payload_final = "general"
            except ValueError:
                payload_final = "general"

        return {
            "intent": intent_detectado,
            "entity": payload_final,
            "original": frase_usuario
        }

async def runtime_ulc():
    print(f"📥 Cargando kernel v4.4 (Strict Anchoring)...")
    try:
        model = Model(PATH_MODELO_VOSK)
        recognizer = KaldiRecognizer(model, SAMPLE_RATE)
        engine = ArgentumStaffEngineV44()
    except Exception as e:
        print(f"❌ Error JNI: {e}"); return

    print("\n==========================================================================")
    print(" 🚀 THAMIS LAB — ENGINE ARGENTUM ULC v4.4 (STRICT ANCHORING)")
    print("==========================================================================")
    print("📡 Filtro de Bigramas Activo | Street Forcing | Purgado de Meteoritos.")
    print("Probá: 'llevame a callao' vs 'llevar empanadas'...")
    print("==========================================================================\n")

    audio_queue = asyncio.Queue()
    loop = asyncio.get_running_loop()
    def cb(indata, f, t, s): loop.call_soon_threadsafe(audio_queue.put_nowait, indata.copy())
    stream = sd.InputStream(samplerate=SAMPLE_RATE, channels=CHANNELS, blocksize=CHUNK_SIZE, callback=cb, dtype='int16')

    with stream:
        while True:
            pcm = await audio_queue.get()
            if recognizer.AcceptWaveform(pcm.tobytes()):
                text = json.loads(recognizer.Result()).get("text", "").strip()
                if text:
                    contrato = engine.resolver_contrato(text)
                    print(f"✨ [CONTRATO JSON v4.4]:\n{json.dumps(contrato, indent=2, ensure_ascii=False)}")
                    print("--------------------------------------------------------------------------")
            audio_queue.task_done()

if __name__ == "__main__":
    try: asyncio.run(runtime_ulc())
    except KeyboardInterrupt: print("\n🛑 Cerrado.")
