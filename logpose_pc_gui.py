import socket
import json
import tkinter as tk
from tkinter import ttk, scrolledtext, messagebox
import threading
import requests
import os
import math
import random
import time
import subprocess
import psutil
import wave
import struct
import numpy as np
from io import BytesIO
from PIL import Image, ImageTk
from datetime import datetime

"""
THAMIS LAB — Professional Engineering Suite (Cyber-Engineering)
--------------------------------------------------------------
Versión Consolidada: Frontend de Alta Fidelidad + Lógica de Ingeniería
"""

# ----------------------------------------------------------------------
# CONFIGURACIÓN TÉCNICA
# ----------------------------------------------------------------------
UDP_PORT        = 9999
LAB_CMD_PORT    = 5051
BRAIN_URL       = "http://localhost:5000"
ADB_PATH        = "adb"

# ----------------------------------------------------------------------
# PALETA / TOKENS DE DISEÑO
# ----------------------------------------------------------------------
BG_ROOT      = "#020202"   # negro base
BG_PANEL     = "#0A0E0E"   # negro panel
BG_CARD      = "#0D1212"   # tarjeta
BORDER       = "#1C2626"   # borde fino
BORDER_ACT   = "#00CEC9"   # borde activo
CYAN         = "#00CEC9"   # acento primario
CYAN_DIM     = "#0A4A48"   # acento apagado
TEXT_MAIN    = "#E7FFFE"   # texto principal
TEXT_DIM     = "#5C7A78"   # texto secundario
TEXT_FAINT   = "#33453F"   # texto terciario
WARN         = "#FFB020"   # ámbar
DANGER       = "#FF4C4C"   # rojo
OK           = "#3DDC97"   # verde
PURPLE       = "#A29BFE"   # púrpura (IA)
WHITE        = "#FFFFFF"   # blanco puro

FONT_MONO       = ("Consolas", 10)
FONT_MONO_BOLD  = ("Consolas", 10, "bold")
FONT_TITLE      = ("Consolas", 13, "bold")
FONT_LABEL      = ("Consolas", 9)
FONT_METRIC     = ("Consolas", 22, "bold")
FONT_MICRO      = ("Consolas", 8)

# ----------------------------------------------------------------------
# WIDGETS REUTILIZABLES
# ----------------------------------------------------------------------
class Card(tk.Frame):
    def __init__(self, parent, title=None, subtitle=None, **kwargs):
        super().__init__(
            parent, bg=BG_CARD, highlightbackground=BORDER,
            highlightthickness=1, bd=0, **kwargs
        )
        self.content = tk.Frame(self, bg=BG_CARD)
        self._build_header(title, subtitle)
        self.content.pack(fill="both", expand=True, padx=14, pady=(0, 14))

    def _build_header(self, title, subtitle):
        if not title: return
        header = tk.Frame(self, bg=BG_CARD)
        header.pack(fill="x", padx=14, pady=(12, 6))
        marker = tk.Frame(header, bg=CYAN, width=3, height=14)
        marker.pack(side="left", padx=(0, 8))
        tk.Label(header, text=title.upper(), font=FONT_MONO_BOLD, fg=TEXT_MAIN, bg=BG_CARD).pack(side="left", anchor="w")
        if subtitle:
            tk.Label(header, text=subtitle, font=FONT_MICRO, fg=TEXT_DIM, bg=BG_CARD).pack(side="right", anchor="e")
        sep = tk.Frame(self, bg=BORDER, height=1)
        sep.pack(fill="x", padx=14, pady=(0, 8))

class MetricTile(tk.Frame):
    def __init__(self, parent, label, value, unit="", status="ok", **kwargs):
        super().__init__(parent, bg=BG_CARD, highlightbackground=BORDER, highlightthickness=1, **kwargs)
        self.status_color = {"ok": OK, "warn": WARN, "danger": DANGER}.get(status, OK)
        top = tk.Frame(self, bg=BG_CARD)
        top.pack(fill="x", padx=12, pady=(10, 0))
        tk.Label(top, text=label.upper(), font=FONT_MICRO, fg=TEXT_DIM, bg=BG_CARD).pack(side="left")
        self.dot = tk.Canvas(top, width=8, height=8, bg=BG_CARD, highlightthickness=0)
        self.dot_id = self.dot.create_oval(1, 1, 7, 7, fill=self.status_color, outline="")
        self.dot.pack(side="right")
        body = tk.Frame(self, bg=BG_CARD)
        body.pack(fill="x", padx=12, pady=(2, 12))
        self.value_label = tk.Label(body, text=value, font=FONT_METRIC, fg=TEXT_MAIN, bg=BG_CARD)
        self.value_label.pack(side="left", anchor="s")
        if unit:
            tk.Label(body, text=unit, font=FONT_LABEL, fg=TEXT_DIM, bg=BG_CARD).pack(side="left", anchor="s", padx=(4, 0), pady=(0, 4))

    def update_value(self, new_val, status=None):
        self.value_label.config(text=str(new_val))
        if status:
            color = {"ok": OK, "warn": WARN, "danger": DANGER}.get(status, OK)
            self.dot.itemconfig(self.dot_id, fill=color)

class CyberButton(tk.Label):
    def __init__(self, parent, text, command=None, primary=False, **kwargs):
        bg = CYAN_DIM if primary else BG_PANEL
        fg = CYAN if primary else TEXT_DIM
        super().__init__(
            parent, text=f"  {text}  ", font=FONT_MONO_BOLD, bg=bg, fg=fg,
            highlightbackground=CYAN if primary else BORDER,
            highlightthickness=1, cursor="hand2", padx=6, pady=6, **kwargs
        )
        self._command = command
        self._bg = bg
        self._primary = primary
        self.bind("<Enter>", self._on_enter)
        self.bind("<Leave>", self._on_leave)
        self.bind("<Button-1>", self._on_click)

    def _on_enter(self, _e): self.configure(bg=CYAN, fg="#001010")
    def _on_leave(self, _e): self.configure(bg=self._bg, fg=CYAN if self._primary else TEXT_DIM)
    def _on_click(self, _e):
        if self._command: self._command()

def styled_slider(parent, label, from_, to, initial, unit="", logic_callback=None):
    frame = tk.Frame(parent, bg=BG_CARD)
    top = tk.Frame(frame, bg=BG_CARD)
    top.pack(fill="x")
    tk.Label(top, text=label.upper(), font=FONT_MICRO, fg=TEXT_DIM, bg=BG_CARD).pack(side="left")
    value_lbl = tk.Label(top, text=f"{initial}{unit}", font=FONT_MONO_BOLD, fg=CYAN, bg=BG_CARD)
    value_lbl.pack(side="right")

    def on_move(v):
        val = float(v)
        value_lbl.configure(text=f"{val:.0f}{unit}")
        if logic_callback: logic_callback(val)

    scale = tk.Scale(
        frame, from_=from_, to=to, orient="horizontal", showvalue=False,
        bg=BG_CARD, fg=CYAN, troughcolor=BG_PANEL, highlightthickness=0,
        activebackground=CYAN, sliderrelief="flat", bd=0, command=on_move
    )
    scale.set(initial)
    scale.pack(fill="x", pady=(4, 0))
    return frame, scale

# ----------------------------------------------------------------------
# APP PRINCIPAL
# ----------------------------------------------------------------------
class ThamisLabApp(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("THAMIS LAB — v50.0 PROFESSIONAL")
        self.geometry("1400x900")
        self.configure(bg=BG_ROOT)

        # --- ESTADOS DE INGENIERÍA ---
        self.is_lab_active = False
        self.is_recording = False
        self.stop_recording_event = threading.Event()
        self.cpu_history = [0] * 100
        self.last_voice_text = ""

        self._configure_ttk_style()
        self._build_topbar()
        self._build_notebook()
        self._build_statusbar()

        # --- MOTORES DE FONDO ---
        threading.Thread(target=self.logic_hw_monitor, daemon=True).start()
        threading.Thread(target=self.logic_udp_server, daemon=True).start()
        threading.Thread(target=self.logic_adb_refresh, daemon=True).start()

    def _configure_ttk_style(self):
        style = ttk.Style(self); style.theme_use("clam")
        style.configure("TNotebook", background=BG_ROOT, borderwidth=0)
        style.configure("TNotebook.Tab", background=BG_PANEL, foreground=TEXT_DIM, font=FONT_MONO_BOLD, padding=(18, 10), borderwidth=0)
        style.map("TNotebook.Tab", background=[("selected", BG_CARD)], foreground=[("selected", CYAN)])
        style.configure("Treeview", background=BG_CARD, fieldbackground=BG_CARD, foreground=TEXT_MAIN, font=FONT_MONO, borderwidth=0, rowheight=32)
        style.configure("Treeview.Heading", background=BG_PANEL, foreground=CYAN, font=FONT_MONO_BOLD, borderwidth=0, relief="flat")
        style.map("Treeview", background=[("selected", CYAN_DIM)])

    def _build_topbar(self):
        bar = tk.Frame(self, bg=BG_PANEL, height=60); bar.pack(fill="x", side="top"); bar.pack_propagate(False)
        left = tk.Frame(bar, bg=BG_PANEL); left.pack(side="left", padx=18)
        tk.Label(left, text="◈", font=("Consolas", 18), fg=CYAN, bg=BG_PANEL).pack(side="left", padx=(0, 8))
        tk.Label(left, text="THAMIS LAB", font=("Consolas", 16, "bold"), fg=TEXT_MAIN, bg=BG_PANEL).pack(side="left")

        right = tk.Frame(bar, bg=BG_PANEL); right.pack(side="right", padx=18)
        self.adb_combo = ttk.Combobox(right, state="readonly", width=15)
        self.adb_combo.pack(side="left", padx=20)

        self.btn_power = CyberButton(right, "INICIAR LABORATORIO", primary=True, command=self.logic_toggle_mission)
        self.btn_power.pack(side="left")

    def _build_notebook(self):
        container = tk.Frame(self, bg=BG_ROOT); container.pack(fill="both", expand=True, padx=16, pady=14)
        self.notebook = ttk.Notebook(container); self.notebook.pack(fill="both", expand=True)

        self.tabs = {
            "ANALÍTICAS": self._make_tab(), "NEURAL FEED": self._make_tab(),
            "SIMULADOR": self._make_tab(), "CONOCIMIENTO": self._make_tab(), "SPRINTS": self._make_tab()
        }
        for name, frame in self.tabs.items(): self.notebook.add(frame, text=f"  {name}  ")

        self._build_dashboard(self.tabs["ANALÍTICAS"])
        self._build_neural_feed(self.tabs["NEURAL FEED"])
        self._build_chaos_engine(self.tabs["SIMULADOR"])
        self._build_voice_lab(self.tabs["CONOCIMIENTO"])
        self._build_progress(self.tabs["SPRINTS"])

    def _make_tab(self): return tk.Frame(self.notebook, bg=BG_ROOT)

    # ------------------------------------------------------------------
    # TABS & UI LOGIC
    # ------------------------------------------------------------------
    def _build_dashboard(self, root):
        root.grid_columnconfigure((0, 1, 2, 3), weight=1, uniform="col")
        self.tiles = {
            "CPU": MetricTile(root, "CPU", "0", "%"), "RAM": MetricTile(root, "RAM", "0", "MB"),
            "BAT": MetricTile(root, "BATERÍA", "88", "%"), "LAT": MetricTile(root, "LATENCIA", "142", "ms", "warn")
        }
        for i, (key, tile) in enumerate(self.tiles.items()): tile.grid(row=0, column=i, sticky="nsew", padx=8, pady=(0, 12))

        status_card = Card(root, title="Estado de LogPose", subtitle="Live Telemetry")
        status_card.grid(row=1, column=0, columnspan=2, sticky="nsew", padx=(0, 8))
        self.status_rows = {}
        for label in ["Wake-word", "Sesión de manejo", "Spotify", "Última automatización"]:
            r = tk.Frame(status_card.content, bg=BG_CARD); r.pack(fill="x", pady=5)
            tk.Label(r, text=label, font=FONT_LABEL, fg=TEXT_DIM, bg=BG_CARD).pack(side="left")
            lbl = tk.Label(r, text="--", font=FONT_MONO_BOLD, fg=TEXT_MAIN, bg=BG_CARD)
            lbl.pack(side="right"); self.status_rows[label] = lbl

        graph_card = Card(root, title="Actividad de Procesamiento", subtitle="Carga CPU real")
        graph_card.grid(row=1, column=2, columnspan=2, sticky="nsew", padx=(8, 0))
        self.canvas_graph = tk.Canvas(graph_card.content, bg=BG_PANEL, highlightthickness=0, height=180)
        self.canvas_graph.pack(fill="both", expand=True)

    def _build_neural_feed(self, root):
        root.grid_columnconfigure(0, weight=3); root.grid_rowconfigure(0, weight=1)
        feed_card = Card(root, title="Neural Feed", subtitle="Protocolo unificado")
        feed_card.grid(row=0, column=0, sticky="nsew", padx=(0, 8))

        self.feed_text = scrolledtext.ScrolledText(feed_card.content, bg=BG_PANEL, fg=TEXT_MAIN, font=FONT_MONO, bd=0, padx=10, pady=10)
        self.feed_text.pack(fill="both", expand=True)
        for tag, color in [("sys", TEXT_DIM), ("ok", OK), ("warn", WARN), ("err", DANGER), ("ia", PURPLE), ("user", CYAN)]:
            self.feed_text.tag_configure(tag, foreground=color)

        input_row = tk.Frame(feed_card.content, bg=BG_CARD); input_row.pack(fill="x", pady=(10, 0))
        self.chat_entry = tk.Entry(input_row, bg=BG_PANEL, fg=TEXT_MAIN, insertbackground=CYAN, font=FONT_MONO, bd=0, highlightthickness=1, highlightbackground=BORDER)
        self.chat_entry.pack(side="left", fill="x", expand=True, ipady=8, padx=(0, 8))
        self.chat_entry.bind("<Return>", lambda e: self.logic_send_chat())
        CyberButton(input_row, "ENVIAR", primary=True, command=self.logic_send_chat).pack(side="right")

    def _build_chaos_engine(self, root):
        sliders_card = Card(root, title="Simulador de Entorno", subtitle="Variables Físicas")
        sliders_card.pack(side="left", fill="both", expand=True, padx=(0, 8))
        for label, lo, hi, init, unit in [("Viento", 0, 150, 0, " km/h"), ("Lluvia", 0, 100, 0, " %"), ("Velocidad", 0, 200, 0, " km/h"), ("Inclinación", -45, 45, 0, "°")]:
            f, _ = styled_slider(sliders_card.content, label, lo, hi, init, unit, lambda v, l=label: self.logic_sim_update(l, v))
            f.pack(fill="x", pady=12)

        mirror_card = Card(root, title="Espejo Real ADB", subtitle="Streaming")
        mirror_card.pack(side="right", fill="both", expand=True)
        self.lbl_mirror = tk.Label(mirror_card.content, bg="black", text="START MIRROR TO VIEW", fg=TEXT_FAINT); self.lbl_mirror.pack(fill="both", expand=True)
        CyberButton(mirror_card.content, "START MIRROR", command=self.logic_toggle_mirror).pack(pady=10)

    def _build_voice_lab(self, root):
        wave_card = Card(root, title="Visualizador de Audio", subtitle="Entrada MIC")
        wave_card.pack(side="left", fill="both", expand=True, padx=(0, 8))
        self.wave_canvas = tk.Canvas(wave_card.content, bg=BG_PANEL, highlightthickness=0); self.wave_canvas.pack(fill="both", expand=True)

        btn_f = tk.Frame(wave_card.content, bg=BG_CARD); btn_f.pack(fill="x", pady=10)
        self.btn_rec = CyberButton(btn_f, "● INICIAR ESCUCHA", primary=True, command=self.logic_toggle_voice)
        self.btn_rec.pack(side="left")

        res_card = Card(root, title="Interpretación IA", width=400)
        res_card.pack(side="right", fill="both")
        res_card.pack_propagate(False)
        self.voice_res_lbl = tk.Label(res_card.content, text="Esperando...", font=FONT_METRIC, fg=CYAN, bg=BG_CARD, wraplength=350)
        self.voice_res_lbl.pack(pady=40)
        tk.Label(res_card.content, text="Corregir si es necesario:", fg=TEXT_DIM, bg=BG_CARD).pack(anchor="w")
        self.voice_corr = tk.Entry(res_card.content, bg=BG_PANEL, fg=WHITE, font=FONT_MONO, bd=0, highlightthickness=1); self.voice_corr.pack(fill="x", pady=10, ipady=8)
        CyberButton(res_card.content, "GUARDAR APRENDIZAJE", command=self.logic_save_learning).pack(fill="x")

    def _build_progress(self, root):
        tree_card = Card(root, title="Knowledge Base", subtitle="Aprendizaje acumulado")
        tree_card.pack(fill="both", expand=True)
        cols = ("fecha", "categoria", "descripcion", "estado")
        self.learn_tree = ttk.Treeview(tree_card.content, columns=cols, show="headings")
        for c in cols: self.learn_tree.heading(c, text=c.upper()); self.learn_tree.column(c, width=150)
        self.learn_tree.pack(fill="both", expand=True)

    def _build_statusbar(self):
        bar = tk.Frame(self, bg=BG_PANEL, height=28); bar.pack(fill="x", side="bottom")
        self.lbl_status_msg = tk.Label(bar, text="THAMIS LAB v50.0  ·  READY", font=FONT_MICRO, fg=TEXT_FAINT, bg=BG_PANEL)
        self.lbl_status_msg.pack(side="left", padx=14)

    # ------------------------------------------------------------------
    # ENGINEERING LOGIC (BACKEND)
    # ------------------------------------------------------------------
    def logic_toggle_mission(self):
        self.is_lab_active = not self.is_lab_active
        if self.is_lab_active:
            self.btn_power.config(text="DETENER LABORATORIO", bg=DANGER)
            self.logic_add_log("sys", "Laboratorio en línea. Secuencia de inicio completada.")
        else:
            self.btn_power.config(text="INICIAR LABORATORIO", bg=CYAN_DIM)

    def logic_add_log(self, tag, msg):
        ts = datetime.now().strftime("%H:%M:%S")
        self.feed_text.configure(state="normal")
        self.feed_text.insert("end", f"[{ts}] ", "sys")
        self.feed_text.insert("end", f"{tag.upper().ljust(8)} ", tag)
        self.feed_text.insert("end", f"{msg}\n")
        self.feed_text.configure(state="disabled"); self.feed_text.see("end")

    def logic_send_chat(self):
        msg = self.chat_entry.get()
        if not msg: return
        self.logic_add_log("user", msg); self.chat_entry.delete(0, tk.END)
        threading.Thread(target=self._claude_request, args=(msg,), daemon=True).start()

    def _claude_request(self, msg):
        try:
            resp = requests.post(f"{BRAIN_URL}/chat", json={"msg": msg}, timeout=5)
            if resp.status_code == 200: self.after(0, lambda: self.logic_add_log("ia", resp.json().get("claude")))
        except: self.after(0, lambda: self.logic_add_log("err", "Cerebro desconectado."))

    def logic_hw_monitor(self):
        while True:
            cpu = psutil.cpu_percent()
            ram = psutil.virtual_memory().used // (1024*1024)
            self.cpu_history.pop(0); self.cpu_history.append(cpu)
            self.after(0, lambda: [self.tiles["CPU"].update_value(cpu), self.tiles["RAM"].update_value(ram)])
            time.sleep(1)

    def logic_sim_update(self, label, val):
        self.logic_add_log("sys", f"Simulation Parameter Adjusted: {label} -> {val}")
        serial = self.adb_combo.get()
        if serial:
            subprocess.run([ADB_PATH, "-s", serial, "shell", "am", "broadcast", "-a", "com.uriel.logpose.SIM_UPDATE", "--ef", label.lower(), str(val)])

    def logic_toggle_voice(self):
        if not self.is_recording:
            self.is_recording = True; self.btn_rec.config(text="⏹ DETENER", bg=DANGER)
            threading.Thread(target=self._voice_task, daemon=True).start()
        else:
            self.is_recording = False; self.btn_rec.config(text="● INICIAR ESCUCHA", bg=CYAN_DIM)

    def _voice_task(self):
        try:
            from pvrecorder import PvRecorder
            import speech_recognition as sr
            recorder = PvRecorder(device_index=-1, frame_length=512); recorder.start(); audio_data = []
            while self.is_recording:
                frame = recorder.read(); audio_data.extend(frame)
                rms = np.sqrt(np.mean(np.array(frame, dtype=np.int32)**2))
                if rms < 350 and len(audio_data) > 32000: break
            recorder.stop(); recorder.delete()
            buffer = BytesIO()
            with wave.open(buffer, 'wb') as f:
                f.setparams((1, 2, 16000, 512, "NONE", "not compressed")); f.writeframes(struct.pack("h" * len(audio_data), *audio_data))
            buffer.seek(0); r = sr.Recognizer()
            with sr.AudioFile(buffer) as source: audio = r.record(source)
            text = r.recognize_google(audio, language="es-AR")
            self.after(0, lambda: self._show_voice(text))
        except Exception as e: self.logic_add_log("err", f"Audio Error: {e}")

    def _show_voice(self, text):
        self.voice_res_lbl.config(text=f"\"{text}\"")
        self.logic_add_log("voice", f"Recognized: {text}")

    def logic_save_learning(self):
        orig = self.voice_res_lbl.cget("text"); corr = self.voice_corr.get()
        if corr:
            self.learn_tree.insert("", 0, values=(datetime.now().strftime("%H:%M"), "VOICE", f"{orig} -> {corr}", "LEARNED"))
            self.logic_add_log("ia", f"Knowledge Persisted: phonetic correction saved.")
            self.voice_corr.delete(0, tk.END)

    def logic_toggle_mirror(self):
        # Implementation for scrcpy or screen capture
        self.logic_add_log("sys", "ADB Mirror link requested...")

    def logic_adb_refresh(self):
        while True:
            try:
                res = subprocess.run([ADB_PATH, "devices"], capture_output=True, text=True).stdout.strip().split("\n")[1:]
                devs = [d.split()[0] for d in res if d.strip()]
                self.adb_combo["values"] = devs
                if devs and not self.adb_combo.get(): self.adb_combo.set(devs[0])
            except: pass
            time.sleep(5)

    def logic_udp_server(self):
        with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as s:
            s.bind(("0.0.0.0", UDP_PORT))
            while True:
                data, _ = s.recvfrom(2048)
                self.after(0, lambda m=data.decode('utf-8'): self.logic_add_log("rider", m))

if __name__ == "__main__":
    app = ThamisLabApp(); app.mainloop()
