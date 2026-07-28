import socket
import threading
import tkinter as tk
from tkinter import scrolledtext
import os
import ctypes
import json
import re
from datetime import datetime

# Intentamos cargar librerías de voz
try:
    import sounddevice as sd
    from vosk import Model, KaldiRecognizer
    import numpy as np
    VOICE_AVAILABLE = True
except ImportError:
    VOICE_AVAILABLE = False

# Importamos controladores
from netflix_controller import NetflixController
from youtube_controller import YoutubeController
from volume_controller import VolumeController

class ThamisPC:
    def __init__(self, glosario):
        self.glosario = glosario
        self.training_file = "training_log.txt"

    def process(self, text):
        raw = text.lower().strip()
        if not raw: return None, None
        
        # 1. Corrección Fonética
        clean = self.apply_corrections(raw)
        
        # 2. Detección de Intención
        intent = "unknown"
        target = clean
        
        if self.match(clean, "control.activar"): intent = "activar"
        elif self.match(clean, "control.desactivar"): intent = "desactivar"
        elif self.match(clean, "verbos.abrir"):
            intent = "abrir"
            target = self.resolve_app(clean)
        elif self.match(clean, "verbos.reproducir"):
            intent = "reproducir"
            target = self.clean_query(clean)
        elif self.match(clean, "verbos.volumen_subir"): intent = "volumen_subir"
        elif self.match(clean, "verbos.volumen_bajar"): intent = "volumen_bajar"
        
        # 3. Entrenamiento: Si es desconocido y largo, lo guardamos
        if intent == "unknown" and len(raw) > 3:
            self.log_for_training(raw)
            
        return intent, target

    def apply_corrections(self, text):
        t = text
        for cat in ["artistas", "titulos_netflix"]:
            mapping = self.glosario.get(cat, {})
            for correct, variants in mapping.items():
                for v in variants:
                    if v in t: t = t.replace(v, correct)
        return t

    def match(self, text, path):
        p = path.split(".")
        nodes = self.glosario.get(p[0], {}).get(p[1], [])
        return any(n in text for n in nodes)

    def resolve_app(self, text):
        for app, vars in self.glosario.get("apps", {}).items():
            if any(v in text for v in vars): return app
        return "youtube"

    def clean_query(self, text):
        q = text
        for w in self.glosario.get("verbos", {}).get("reproducir", []) + [" el ", " la ", " un ", " una ", " de "]:
            q = q.replace(w, " ")
        return q.strip()

    def log_for_training(self, text):
        try:
            with open(self.training_file, "a", encoding="utf-8") as f:
                f.write(f"[{datetime.now().strftime('%Y-%m-%d %H:%M')}] {text}\n")
        except: pass

class LogPoseApp:
    def __init__(self, root):
        self.root = root
        self.root.title("LogPose OS - Terminal Thamis Pro")
        self.root.geometry("800x650")
        self.root.configure(bg="#1e1e1e")
        
        # UI Consola
        self.console = scrolledtext.ScrolledText(root, bg="#000000", fg="#dcdde1", font=("Consolas", 10))
        self.console.pack(fill="both", expand=True, padx=15, pady=15)
        self.console.tag_config("green", foreground="#55efc4")
        self.console.tag_config("yellow", foreground="#ffeaa7")
        self.console.tag_config("red", foreground="#ff7675")
        self.console.tag_config("cyan", foreground="#81ecec")
        
        self.is_active = True
        self.glosario = self.load_glosario()
        self.thamis = ThamisPC(self.glosario)
        
        self.log("SISTEMA LOGPOSE PC: INICIANDO...", "yellow")
        threading.Thread(target=self.init_hardware, daemon=True).start()

    def load_glosario(self):
        try:
            with open("logpose_glosario.json", "r", encoding="utf-8") as f:
                return json.load(f)
        except:
            self.log("!!! ERROR: logpose_glosario.json no encontrado.", "red")
            return {}

    def log(self, msg, tag="white"):
        t = datetime.now().strftime("%H:%M:%S")
        self.console.insert(tk.END, f"[{t}] [SYS] {msg}\n", tag)
        self.console.see(tk.END)

    def init_hardware(self):
        from selenium.webdriver.chrome.options import Options
        from selenium import webdriver
        
        opts = Options()
        opts.add_argument(f"--user-data-dir={os.path.join(os.getcwd(), 'Thamis_Chrome_Profile')}")
        opts.add_experimental_option("detach", True)
        
        try:
            self.driver = webdriver.Chrome(options=opts)
            self.ctrls = {
                "netflix": NetflixController(self.driver),
                "youtube": YoutubeController(self.driver),
                "volumen": VolumeController()
            }
            self.log("Controladores Multimedia: OK", "green")
        except:
            self.log("WARN: Selenium no pudo iniciar. ¿Chrome abierto?", "yellow")
            self.ctrls = {"volumen": VolumeController()}

        if VOICE_AVAILABLE:
            self.start_voice()
        
        # Servidor UDP para el celular
        threading.Thread(target=self.udp_server, daemon=True).start()

    def start_voice(self):
        path = "model_pc"
        if not os.path.exists(path):
            self.log(f"ERROR: Falta carpeta '{path}'.", "red")
            return

        self.log(f"Cargando Big Brain ({path})... Espere.", "yellow")
        try:
            model = Model(path)
            # Extraer gramática para precisión 100%
            words = []
            for c in self.glosario.values():
                if isinstance(c, dict):
                    for v in c.values(): words.extend(v if isinstance(v, list) else [])
            
            grammar = json.dumps(list(set(words + ["netflix", "youtube", "reproducir", "abrí", "activar", "desactivar"])))
            self.rec = KaldiRecognizer(model, 16000, grammar)
            self.log("¡CEREBRO THAMIS PC ONLINE!", "green")
            
            with sd.RawInputStream(samplerate=16000, blocksize=4000, dtype='int16', channels=1) as stream:
                while True:
                    data, _ = stream.read(4000)
                    if self.rec.AcceptWaveform(bytes(data)):
                        res = json.loads(self.rec.Result())
                        txt = res.get("text", "")
                        if txt: self.root.after(0, self.execute, txt, "VOZ")
        except Exception as e:
            self.log(f"Error Motor Voz: {e}", "red")

    def execute(self, text, source="RED"):
        intent, target = self.thamis.process(text)
        
        if intent == "activar":
            self.is_active = True
            self.log("THAMIS: Despierta y escuchando.", "green")
            return
        if intent == "desactivar":
            self.is_active = False
            self.log("THAMIS: En modo standby (Ignorando comandos).", "red")
            return
            
        if not self.is_active:
            if len(text) > 2: self.log(f"Ignorado (Standby): {text}", "cyan")
            return

        if not intent or intent == "unknown":
            self.log(f"No entendí: '{text}' (Guardado para entrenamiento)", "yellow")
            return

        self.log(f"{source}: {text} -> {intent.upper()} ({target})", "cyan")
        
        try:
            if intent == "abrir":
                self.ctrls.get(target, self.ctrls["youtube"]).abrir()
            elif intent == "reproducir":
                # Si Thamis PC detectó netflix en la frase, usamos ese controller
                app = "youtube"
                if "netflix" in text.lower(): app = "netflix"
                self.ctrls[app].buscar_y_reproducir(target)
            elif intent == "volumen_subir": self.ctrls["volumen"].subir()
            elif intent == "volumen_bajar": self.ctrls["volumen"].bajar()
        except Exception as e:
            self.log(f"Error ejecución: {e}", "red")

    def udp_server(self):
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        sock.bind(("0.0.0.0", 5050))
        while True:
            data, _ = sock.recvfrom(4096)
            try:
                cmd = json.loads(data.decode("utf-8"))
                target = cmd.get("target", "")
                if target: self.root.after(0, self.execute, target, "CELULAR")
            except: pass

if __name__ == "__main__":
    root = tk.Tk()
    app = LogPoseApp(root)
    root.mainloop()
