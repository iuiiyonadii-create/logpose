import asyncio
import websockets
import json
import os
from deepgram import DeepgramClient, LiveOptions, LiveTranscriptionEvents

# Configuración de Deepgram
# Se recomienda setear la variable de entorno DEEPGRAM_API_KEY
DEEPGRAM_API_KEY = os.getenv("DEEPGRAM_API_KEY", "TU_API_KEY_AQUI")
deepgram = DeepgramClient(DEEPGRAM_API_KEY)

async def audio_stream_handler(websocket, path):
    print("🏍️ Xiaomi LogPose4 conectado al laboratorio de audio.")
    
    options = LiveOptions(
        model="nova-2-phonecall",
        language="es",
        smart_format=True,
        encoding="linear16",
        channels=1,
        sample_rate=16000
    )
    
    try:
        dg_connection = deepgram.listen.live.v("1", options)
        
        def on_message(self, result, **kwargs):
            sentence = result.channel.alternatives[0].transcript
            if sentence.strip():
                # Enviar de vuelta al celular
                loop = asyncio.get_event_loop()
                coro = websocket.send(json.dumps({"text": sentence}))
                asyncio.run_coroutine_threadsafe(coro, loop)
                print(f"✨ Transcripción Perfecta: {sentence}")

        dg_connection.on(LiveTranscriptionEvents.TranscriptReceived, on_message)
        dg_connection.start()

        async for message in websocket:
            dg_connection.send(message)
            
        dg_connection.finish()
            
    except Exception as e:
        print(f"❌ Error en el streaming: {e}")
    finally:
        print("🔌 Conexión cerrada con el dispositivo.")

async def main():
    async with websockets.serve(audio_stream_handler, "0.0.0.0", 8089):
        print("🚀 Servidor STT Híbrido escuchando en el puerto 8089...")
        await asyncio.Future()  # run forever

if __name__ == "__main__":
    asyncio.run(main())
