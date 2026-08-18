from flask import Flask, request, jsonify
import requests
import json

app = Flask(__name__)

# ============================================================
#  CLAUCODE GRATIS — THAMIS PC BRAIN v1.0
# ============================================================
OLLAMA_URL = "http://localhost:11434/api/generate"
MODELO_OLLAMA = "qwen2.5:0.5b" # Rápido y ligero para comandos

SYSTEM_PROMPT = """
You are 'ClauCode Gratis', the high-precision intent resolver for LogPose (a motorcycle assistant).
Analyze the input text which might be noisy rioplatense Spanish.

Rules:
1. Resolve the intent: PLAY_MUSIC, NAVIGATE, CALL_CONTACT, STOP_NAVIGATION, SET_VOLUME, UNKNOWN.
2. Extract the entity (clean song title, contact name, or destination).
3. If the text is very noisy (e.g. 'los poneos'), try to rescue it if the intent is clear.
4. Return ONLY a valid JSON object.

Format:
{
  "intent": "INTENT_NAME",
  "entity": "clean_entity",
  "confidence": 0.0-1.0,
  "thinking": "Brief explanation of your reasoning"
}
"""

@app.route('/chat', methods=['POST'])
def chat():
    data = request.json
    user_msg = data.get("msg", "")
    
    if not user_msg:
        return jsonify({"error": "Empty message"}), 400

    print(f"🧠 ClauCode receiving: '{user_msg}'")
    
    payload = {
        "model": MODELO_OLLAMA,
        "prompt": f"{SYSTEM_PROMPT}\n\nUser Input: '{user_msg}'",
        "stream": False,
        "format": "json"
    }

    try:
        res = requests.post(OLLAMA_URL, json=payload, timeout=10.0)
        if res.status_code == 200:
            ollama_response = res.json().get("response", "{}")
            response_data = json.loads(ollama_response)
            
            # Formato compatible con el app (ThamisHttpConnector)
            return jsonify({
                "claude": response_data.get("intent", "UNKNOWN"),
                "thinking": response_data.get("thinking", "No reasoning provided"),
                "entity": response_data.get("entity", ""),
                "confidence": response_data.get("confidence", 0.0)
            })
        else:
            return jsonify({"error": f"Ollama error: {res.status_code}"}), 500
    except Exception as e:
        print(f"❌ Connection error: {e}")
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    print("🚀 ClauCode Gratis Server running on http://0.0.0.0:5000")
    app.run(host='0.0.0.0', port=5000)
