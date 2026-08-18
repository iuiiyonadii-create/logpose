import asyncio
import sys
import json
import numpy as np

try:
    import sounddevice as sd
    import requests
    from vosk import Model, KaldiRecognizer
except ImportError:
    print("❌ Faltan dependencias. Ejecutá: pip install sounddevice requests vosk numpy")
    sys.exit(1)

# ============================================================
#  CONFIGURACIÓN INMUTABLE DEL LABORATORIO ATÓMICO (100% PC)
# ============================================================
SAMPLE_RATE = 16000
CHANNELS = 1
CHUNK_SIZE = 1024  # Cuadros de 64ms para un enganche fonético limpio

OLLAMA_URL = "http://localhost:11434/api/generate"
MODELO_OLLAMA = "qwen2.5:0.5b"  # El modelo ultra-veloz de 397MB que ya tenés descargado

# Forzamos la carga del modelo local comprimido que ya sabemos que inicia en 0ms
PATH_MODELO_VOSK = "C:\\projects\\LogPose4\\app\\src\\main\\assets\\model-es"

print(f"📥 Inicializando Oído Local desde: {PATH_MODELO_VOSK}...")
try:
    model = Model(PATH_MODELO_VOSK)
    recognizer = KaldiRecognizer(model, SAMPLE_RATE)
    recognizer.SetWords(True)
except Exception as e:
    print(f"❌ Error al levantar el motor Vosk: {e}")
    sys.exit(1)

async def bucle_atómico_en_vivo():
    print("\n==========================================================================")
    print(" 🔬 THAMIS LAB — LABORATORIO DE ENTENDIMIENTO ATÓMICO v1.0")
    print("==========================================================================")
    print(f"📡 Escuchando tus auriculares... NLU asignado a Ollama: '{MODELO_OLLAMA}'")
    print("🔒 Entorno: 100% Aislado en PC. 0% contacto con el código del Xiaomi.")
    print("Decí un comando de prueba (ej: 'pone uzbekistan') y hacé silencio...")
    print("==========================================================================\n")

    audio_queue = asyncio.Queue()
    loop = asyncio.get_running_loop()

    def callback_hardware(indata, frames, time_info, status):
        loop.call_soon_threadsafe(audio_queue.put_nowait, indata.copy())

    stream = sd.InputStream(
        samplerate=SAMPLE_RATE,
        channels=CHANNELS,
        blocksize=CHUNK_SIZE,
        callback=callback_hardware,
        dtype='int16'
    )

    with stream:
        while True:
            pcm16_bloque = await audio_queue.get()
            
            # Pasamos los bytes directos al reconocedor
            if recognizer.AcceptWaveform(pcm16_bloque.tobytes()):
                resultado_crudo = json.loads(recognizer.Result())
                texto_escuchado = resultado_crudo.get("text", "").strip()
                
                if texto_escuchado:
                    print(f"\n🗣️ [PASO 1: TU OÍDO ENTENDIÓ]: \"{texto_escuchado}\"")
                    print("⏳ [PASO 2: PROCESANDO INTENCIÓN] Llamando a Ollama local...")
                    
                    # Prompt ultra simple y básico para evaluar el entendimiento base
                    prompt_sistema = (
                        "Sos el motor de intenciones de la app de moto THAMIS. Analizá la frase. "
                        "Devolvé estrictamente un JSON con este formato y nada más de texto:\n"
                        '{"action": "MEDIA_PLAY"|"NAVIGATE"|"UNKNOWN", "target": "<entidad_limpia>"}'
                    )
                    
                    payload = {
                        "model": MODELO_OLLAMA,
                        "prompt": f"{prompt_sistema}\n\nFrase del usuario: '{texto_escuchado}'",
                        "stream": False,
                        "format": "json"
                    }
                    
                    try:
                        res = requests.post(OLLAMA_URL, json=payload, timeout=5.0)
                        if res.status_code == 200:
                            respuesta_json = res.json().get("response", "")
                            print(f"✨ [PASO 3: CEREBRO GENERÓ CONTRATO]:\n{respuesta_json}")
                            print("==========================================================================")
                        else:
                            print(f"⚠️ Alerta: Ollama devolvió estado {res.status_code}")
                    except Exception as e:
                        print(f"❌ Error de enlace con el puerto 11434 de Ollama: {e}")
            else:
                # Transcripción parcial en vivo para que veas que el mic te lee
                parcial = json.loads(recognizer.PartialResult()).get("partial", "").strip()
                if parcial:
                    sys.stdout.write(f"\r🎙️ [Escuchando en vivo]: {partial} ...")
                    sys.stdout.flush()
            
            audio_queue.task_done()

if __name__ == "__main__":
    try:
        asyncio.run(bucle_atómico_en_vivo())
    except KeyboardInterrupt:
        print("\n\n🛑 Laboratorio Atómico cerrado de forma limpia.")
