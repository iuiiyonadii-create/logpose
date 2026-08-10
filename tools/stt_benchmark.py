#!/usr/bin/env python3
"""
THAMIS LAB - Benchmark Comparativo de Motores STT Offline
Uso: python tools/stt_benchmark.py ruta_audio.wav
"""
import os
import sys
import time
import wave
import json

def run_vosk_benchmark(wav_path, model_path="model"):
    try:
        from vosk import Model, Recognizer
        if not os.path.exists(model_path):
            return {"engine": "Vosk", "error": f"Modelo no encontrado en {model_path}"}
        
        t0 = time.perf_counter()
        model = Model(model_path)
        wf = wave.open(wav_path, "rb")
        rec = Recognizer(model, wf.getframerate())
        
        results = []
        while True:
            data = wf.readframes(4000)
            if len(data) == 0: break
            if rec.AcceptWaveform(data):
                res = json.loads(rec.Result())
                if res.get("text"): results.append(res["text"])
        
        final_res = json.loads(rec.FinalResult())
        if final_res.get("text"): results.append(final_res["text"])
        
        t1 = time.perf_counter()
        return {"engine": "Vosk (Offline)", "text": " ".join(results).strip(), "latency_ms": (t1 - t0) * 1000}
    except Exception as e:
        return {"engine": "Vosk", "error": str(e)}

def run_sherpa_onnx_benchmark(wav_path, model_dir="sherpa_models"):
    try:
        import sherpa_onnx
        import numpy as np
        
        encoder = os.path.join(model_dir, "encoder.onnx")
        decoder = os.path.join(model_dir, "decoder.onnx")
        joiner = os.path.join(model_dir, "joiner.onnx")
        tokens = os.path.join(model_dir, "tokens.txt")
        hotwords = os.path.join(model_dir, "hotwords.txt")
        
        t0 = time.perf_counter()
        recognizer = sherpa_onnx.OnlineRecognizer.create_transducer(
            encoder=encoder, decoder=decoder, joiner=joiner, tokens=tokens,
            num_threads=4, sample_rate=16000, feature_dim=80,
            decoding_method="modified_beam_search",
            hotwords_file=hotwords if os.path.exists(hotwords) else "",
            hotwords_score=2.5
        )
        stream = recognizer.create_stream()
        wf = wave.open(wav_path, "rb")
        samples = wf.readframes(wf.getnframes())
        samples_np = np.frombuffer(samples, dtype=np.int16).astype(np.float32) / 32768.0
        stream.accept_waveform(16000, samples_np)
        
        while recognizer.is_ready(stream):
            recognizer.decode_stream(stream)
            
        text = recognizer.get_result(stream)
        t1 = time.perf_counter()
        return {"engine": "Sherpa-ONNX Zipformer (Offline)", "text": text, "latency_ms": (t1 - t0) * 1000}
    except Exception as e:
        return {"engine": "Sherpa-ONNX", "error": str(e)}

def run_whisper_cpp_benchmark(wav_path, model_path="ggml-base.bin"):
    try:
        from pywhispercpp.model import Model
        t0 = time.perf_counter()
        model = Model(model_path, n_threads=4, language="es")
        segments = model.transcribe(wav_path, initial_prompt="Artistas: Duki, Ysy A, Uzbekistán, Milo J, Bizarrap")
        t1 = time.perf_counter()
        text = " ".join([seg.text for seg in segments]).strip()
        return {"engine": "Whisper.cpp (Offline C++)", "text": text, "latency_ms": (t1 - t0) * 1000}
    except Exception as e:
        return {"engine": "Whisper.cpp", "error": str(e)}

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Uso: python tools/stt_benchmark.py <audio.wav>")
        sys.exit(1)
    
    wav = sys.argv[1]
    print(f"=== BENCHMARK DE MOTORES STT OFFLINE: {wav} ===")
    print(json.dumps(run_vosk_benchmark(wav), indent=2, ensure_ascii=False))
    print(json.dumps(run_sherpa_onnx_benchmark(wav), indent=2, ensure_ascii=False))
    print(json.dumps(run_whisper_cpp_benchmark(wav), indent=2, ensure_ascii=False))
