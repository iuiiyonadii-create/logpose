import sys
import subprocess

# Auto-instalar dependencias de audio si faltan
try:
    import speech_recognition as sr
except ImportError:
    print("📦 Instalando biblioteca SpeechRecognition para el micrófono de la PC...")
    subprocess.check_call([sys.executable, "-m", "pip", "install", "SpeechRecognition", "pyaudio"])
    import speech_recognition as sr

import re

def normalize_text(text):
    text = text.lower().strip()
    text = re.sub(r'[áàäâ]', 'a', text)
    text = re.sub(r'[éèëê]', 'e', text)
    text = re.sub(r'[íìïî]', 'i', text)
    text = re.sub(r'[óòöô]', 'o', text)
    text = re.sub(r'[úùüû]', 'u', text)
    return text

def analyze_intent(text):
    norm = normalize_text(text)
    
    # 1. MÚSICA
    music_verbs = ["pone", "pone", "poneme", "reproduce", "reproduci", "reproducir", "play", "musica", "cancion", "tema"]
    for verb in music_verbs:
        if norm.startswith(verb):
            query = norm[len(verb):].strip()
            query = re.sub(r'^(de|en|el|la|los|las|un|una)\s+', '', query)
            query = re.sub(r'\s+en\s+(youtube music|yt music|youtube|spotify)$', '', query)
            return "🎵 MÚSICA & AUDIO (PLAY_MUSIC)", f"Reproducir '{query}' en Spotify/YouTube Music", query

    # 2. NAVEGACIÓN
    nav_verbs = ["llevame", "lleva", "guiame", "navegar", "ir a", "anda a", "camino a", "como llego"]
    for verb in nav_verbs:
        if verb in norm:
            dest = re.sub(r'.*?(' + verb + r')\s*(a|al|hacia)?\s*', '', norm).strip()
            return "🗺️ NAVEGACIÓN GPS (NAVIGATE)", f"Navegar en Google Maps hacia '{dest}'", dest

    # 3. LLAMADAS & SILENCIAR
    if any(w in norm for w in ["silenciar", "silencio", "ignorar", "shh", "mute"]):
        return "🔇 TELEFONÍA (SILENCE_CALL)", "Silenciar timbre de llamada entrante", ""
    if any(w in norm for w in ["atender", "aceptar", "responder llamada"]):
        return "📞 TELEFONÍA (ANSWER_CALL)", "Atender llamada entrante", ""
    if any(w in norm for w in ["rechazar", "cortar", "colgar"]):
        return "📞 TELEFONÍA (REJECT_CALL)", "Rechazar llamada entrante", ""
    if norm.startswith("llama") or norm.startswith("llamar"):
        contact = re.sub(r'^(llama|llamar|llamale)\s+(a)?\s*', '', norm).strip()
        return "📞 TELEFONÍA (CALL_CONTACT)", f"Llamar por teléfono a '{contact}'", contact

    # 4. MENSAJERÍA
    if any(norm.startswith(w) for w in ["manda", "mandale", "envia", "escribi", "mensaje"]):
        parts = re.sub(r'^(manda|mandale|envia|enviame|escribi|escribile|mensaje)\s*', '', norm).strip()
        return "💬 MENSAJERÍA (SEND_MESSAGE)", f"Enviar mensaje de WhatsApp: '{parts}'", parts

    # 5. CLIMA
    if any(w in norm for w in ["clima", "pronostico", "llover", "lluvia", "temperatura", "tiempo"]):
        return "🌤️ CLIMA & RUTA (GET_WEATHER)", "Consultar pronóstico del tiempo en ruta", ""

    # 6. APPS
    if norm.startswith("abrir") or norm.startswith("abre") or norm.startswith("abri"):
        app = re.sub(r'^(abrir|abre|abri)\s*', '', norm).strip()
        return "📱 CONTROL DE APPS (OPEN_APP)", f"Abrir aplicación '{app}'", app

    return "❓ DESCONOCIDO / SIN INTENCIÓN", "No se reconoce comando claro", norm

def main():
    recognizer = sr.Recognizer()
    recognizer.energy_threshold = 300
    recognizer.dynamic_energy_threshold = True

    print("======================================================================")
    print(" 🎙️ PROBADOR DE VOZ DE PC - LOGPOSE / THAMIS (SIN EJECUTAR NADA)")
    print("======================================================================")
    print("El micrófono de tu PC está activo. Hablá una frase...")
    print("(Ejemplos: 'poné duki', 'llevame a la ypf', 'cómo está el clima')\n")

    with sr.Microphone() as source:
        recognizer.adjust_for_ambient_noise(source, duration=1)
        print("✅ Micrófono calibrado. Escuchando...\n")
        
        while True:
            try:
                audio = recognizer.listen(source, timeout=None, phrase_time_limit=5)
                raw_text = recognizer.recognize_google(audio, language="es-AR")
                
                print(f"🗣️ ESCUCHADO RAW : \"{raw_text}\"")
                intent_name, action_desc, payload = analyze_intent(raw_text)
                print(f"🎯 INTENCIÓN     : {intent_name}")
                if payload:
                    print(f"📦 CONTENIDO     : \"{payload}\"")
                print(f"🚀 ACCIÓN TEÓRICA: {action_desc}")
                print("-" * 70 + "\n")
                
            except sr.UnknownValueError:
                pass
            except sr.RequestError as e:
                print(f"⚠️ Error de red o reconocimiento: {e}")
            except KeyboardInterrupt:
                print("\nProbador de PC detenido.")
                break

if __name__ == "__main__":
    main()
