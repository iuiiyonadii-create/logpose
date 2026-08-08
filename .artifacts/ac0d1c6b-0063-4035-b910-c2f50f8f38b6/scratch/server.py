from flask import Flask, request, jsonify
import numpy as np
import whisper
import torch
import os

app = Flask(__name__)

# Seleccionar dispositivo (GPU si está disponible)
device = "cuda" if torch.cuda.is_available() else "cpu"
print(f"🧠 Cargando modelo Whisper (base) en {device}...")
model = whisper.load_model("base", device=device)
print("✅ Whisper Lab listo en puerto 5000")

@app.route('/transcribe', methods=['POST'])
def transcribe():
    try:
        audio_bytes = request.data
        if not audio_bytes:
            return jsonify({"text": ""}), 400

        audio_np = np.frombuffer(audio_bytes, dtype=np.float32).copy()

        # Inferencia de alta precisión
        result = model.transcribe(audio_np, language="es", fp16=False)
        transcription = result.get("text", "").strip()

        print(f"🎙️ Whisper resolvió: '{transcription}'")
        return jsonify({"text": transcription})

    except Exception as e:
        print(f"❌ Error en /transcribe: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route('/process', methods=['POST'])
def process():
    try:
        data = request.json
        prompt = data.get("prompt", "").lower()
        print(f"🧠 Procesando texto: '{prompt}'")
        
        # Mapeador de Intención Staff (Fallback Inteligente)
        intent = "UNKNOWN"
        if any(v in prompt for v in ["pone", "reproduce", "escuchar", "musica", "tema"]):
            intent = "PLAY_MUSIC"
        elif any(v in prompt for v in ["abri", "abrir", "abre", "entra"]):
            intent = "OPEN_APP"
        elif any(v in prompt for v in ["llama", "mensaje", "wasap"]):
            intent = "CALL_CONTACT"

        return jsonify({
            "intent": intent,
            "confidence": 0.95,
            "text": prompt
        })
    except Exception as e:
        print(f"❌ Error en /process: {str(e)}")
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)
