import socket
import json
import tkinter as tk
from tkinter import ttk, scrolledtext, messagebox
import threading
import requests
import os
import time
import subprocess
import psutil
from datetime import datetime

"""
THAMIS LAB OS — MISSION CONTROL CENTER v5.0 MINIMALIST EDITION
--------------------------------------------------------------
Refactorización de Interfaz: Acordeón Dinámico + Terminal Panorámica
"""

# --- CONFIGURACIÓN TÉCNICA ---
UDP_PORT        = 9999
BRAIN_URL       = "http://localhost:5000"
ADB_PATH        = "adb"

# --- PALETA CYBER-MINIMALIST ---
BG_ROOT      = "#0B132B"   # Búnker profundo
BG_SIDEBAR   = "#0D1B2A"   # Sidebar oscuro
BG_PANEL     = "#1C2541"   # Paneles de control
BORDER       = "#3A506B"   # Bordes quirúrgicos
CYAN         = "#00CEC9"   # Acento primario
TEXT_MAIN    = "#E7FFFE"   # Texto principal
TEXT_DIM     = "#5C7A78"   # Texto secundario
DANGER       = "#FF4C4C"   # Rojo falla
OK           = "#3DDC97"   # Verde éxito

FONT_MONO       = ("Consolas", 10)
FONT_MONO_BOLD  = ("Consolas", 10, "bold")
FONT_TITLE      = ("Consolas", 12, "bold")

# ----------------------------------------------------------------------
# WIDGETS PERSONALIZADOS
# ----------------------------------------------------------------------

class AccordionSection(tk.Frame):
    def __init__(self, parent, title, app, **kwargs):
        super().__init__(parent, bg=BG_SIDEBAR, bd=0, **kwargs)
        self.app = app
        self.is_open = False
        
        # Botón de Cabecera
        self.header = tk.Label(
            self, text=f"▶ {title}", font=FONT_MONO_BOLD, bg=BG_SIDEBAR, 
            fg=TEXT_DIM, anchor="w", padx=10, pady=10, cursor="hand2"
        )
        self.header.pack(fill="x")
        self.header.bind("<Button-1>", self.toggle)
        
        # Contenedor de Sub-items
        self.content_frame = tk.Frame(self, bg=BG_SIDEBAR)
        
    def toggle(self, event=None):
        if self.is_open:
            self.content_frame.pack_forget()
            self.header.config(text=self.header.cget("text").replace("▼", "▶"), fg=TEXT_DIM)
        else:
            self.app.close_all_sections()
            self.content_frame.pack(fill="x", padx=20)
            self.header.config(text=self.header.cget("text").replace("▶", "▼"), fg=CYAN)
        self.is_open = not self.is_open

class ActionButton(tk.Label):
    def __init__(self, parent, text, command, **kwargs):
        super().__init__(
            parent, text=f"▫ {text}", font=FONT_MONO, bg=BG_SIDEBAR, 
            fg=TEXT_DIM, anchor="w", padx=5, pady=5, cursor="hand2", **kwargs
        )
        self.command = command
        self.bind("<Enter>", lambda e: self.config(fg=CYAN))
        self.bind("<Leave>", lambda e: self.config(fg=TEXT_DIM))
        self.bind("<Button-1>", lambda e: self.command())
        self.pack(fill="x")

# ----------------------------------------------------------------------
# APLICACIÓN PRINCIPAL
# ----------------------------------------------------------------------

class ThamisLabMinimalist(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("THAMIS LAB OS — v5.0 MINIMALIST EDITION")
        self.geometry("1400x850")
        self.configure(bg=BG_ROOT)
        
        self.sections = []
        self._build_layout()
        
        # Motores de Fondo
        threading.Thread(target=self._logic_hw_monitor, daemon=True).start()
        threading.Thread(target=self._logic_udp_listener, daemon=True).start()

    def _build_layout(self):
        # 1. Top Bar (Métricas)
        self.top_bar = tk.Frame(self, bg=BG_PANEL, height=40, bd=0, highlightbackground=BORDER, highlightthickness=1)
        self.top_bar.pack(fill="x", side="top", padx=5, pady=5)
        
        self.metric_cpu = tk.Label(self.top_bar, text="CPU: 0%", font=FONT_MONO, bg=BG_PANEL, fg=CYAN)
        self.metric_cpu.pack(side="left", padx=20)
        
        self.metric_ram = tk.Label(self.top_bar, text="RAM: 0MB", font=FONT_MONO, bg=BG_PANEL, fg=CYAN)
        self.metric_ram.pack(side="left", padx=20)

        self.lbl_status = tk.Label(self.top_bar, text="● LAB ENGINE: READY", font=FONT_MONO_BOLD, bg=BG_PANEL, fg=OK)
        self.lbl_status.pack(side="right", padx=20)

        # 2. Sidebar (25% Width)
        self.sidebar = tk.Frame(self, bg=BG_SIDEBAR, width=300, bd=0, highlightbackground=BORDER, highlightthickness=1)
        self.sidebar.pack(fill="y", side="left", padx=(5,0), pady=5)
        self.sidebar.pack_propagate(False)

        tk.Label(self.sidebar, text="❖ MISSION CONTROL", font=FONT_TITLE, bg=BG_SIDEBAR, fg=CYAN, pady=20).pack()

        # --- SECCIONES ACORDEÓN ---
        s1 = AccordionSection(self.sidebar, "CORE FUZZING & AUTOMATION", self)
        ActionButton(s1.content_frame, "NLU Fuzzing Lab v6.0", lambda: self.log("Iniciando Fuzzing Lab..."))
        ActionButton(s1.content_frame, "Sherlock Omega v5.0", lambda: self.log("Invocando Sherlock Omega..."))
        ActionButton(s1.content_frame, "Investigación Autónoma", lambda: self.log("Misión Autónoma Iniciada."))
        ActionButton(s1.content_frame, "Auto-Fix Infinito", lambda: self.log("Buscando fallos en rama main..."))
        ActionButton(s1.content_frame, "Auto-Curación & Reparación", lambda: self.log("Ejecutando self-healing local..."))
        ActionButton(s1.content_frame, "Auditoría Libre", lambda: self.log("Auditoría de consistencia iniciada."))
        s1.pack(fill="x"); self.sections.append(s1)

        s2 = AccordionSection(self.sidebar, "BENCHMARK & METRICS", self)
        ActionButton(s2.content_frame, "Stress Test al Límite", lambda: self.log("Ejecutando ráfaga de 100 comandos..."))
        ActionButton(s2.content_frame, "Comparador Multi-Motor", lambda: self.log("Midiendo Vosk vs Sherpa..."))
        ActionButton(s2.content_frame, "Benchmark STT", lambda: self.log("Calculando WER (Word Error Rate)..."))
        ActionButton(s2.content_frame, "Inspector Profiler RAM", lambda: self.log("Analizando heap dump del Xiaomi..."))
        s2.pack(fill="x"); self.sections.append(s2)

        s3 = AccordionSection(self.sidebar, "DEVICE & LOGISTICS", self)
        ActionButton(s3.content_frame, "ADB Logcat en Vivo", lambda: self.log("Streaming de Logcat iniciado (60s)..."))
        ActionButton(s3.content_frame, "Consultar Problema", lambda: self.log("Abriendo ticket de soporte IA..."))
        ActionButton(s3.content_frame, "Inyectar Regla Fonética", lambda: self.log("Glosario Argentum actualizado."))
        ActionButton(s3.content_frame, "Instalar APK en Xiaomi", lambda: self.log("Desplegando build liviana v4.4..."))
        s3.pack(fill="x"); self.sections.append(s3)

        s4 = AccordionSection(self.sidebar, "DEPLOYMENT & PIPELINE", self)
        ActionButton(s4.content_frame, "Suite Tests Gradle", lambda: self.log("Corriendo ./gradlew test..."))
        ActionButton(s4.content_frame, "Self Healing & Git Commit", lambda: self.log("Fixing & Committing..."))
        ActionButton(s4.content_frame, "Iniciar Misión 24h", lambda: self.log("Modo Endurance Activado."))
        ActionButton(s4.content_frame, "Generar Prompt AI de Falla", lambda: self.log("Preparando reporte para Claude..."))
        s4.pack(fill="x"); self.sections.append(s4)

        # Abrir la primera por defecto
        s1.toggle()

        # 3. Terminal Monitor (75% Width)
        self.main_container = tk.Frame(self, bg=BG_ROOT)
        self.main_container.pack(fill="both", expand=True, side="right", padx=5, pady=5)
        
        self.term_header = tk.Frame(self.main_container, bg=BG_ROOT)
        self.term_header.pack(fill="x")
        tk.Label(self.term_header, text="📑 TERMINAL DE DIAGNÓSTICO & EVIDENCIA", font=FONT_MONO_BOLD, bg=BG_ROOT, fg=TEXT_DIM).pack(side="left")
        
        # Botón Limpiar Flotante
        self.btn_clear = tk.Label(self.term_header, text=" [ LIMPIAR ] ", font=FONT_MONO, bg=BG_ROOT, fg=CYAN, cursor="hand2")
        self.btn_clear.pack(side="right")
        self.btn_clear.bind("<Button-1>", lambda e: [self.terminal.config(state="normal"), self.terminal.delete("1.0", "end"), self.terminal.config(state="disabled")])

        self.terminal = scrolledtext.ScrolledText(
            self.main_container, bg="#05080a", fg=TEXT_MAIN, font=FONT_MONO, 
            insertbackground=CYAN, bd=0, highlightbackground=BORDER, highlightthickness=1,
            padx=15, pady=15
        )
        self.terminal.pack(fill="both", expand=True, pady=(5,0))
        self.terminal.config(state="disabled")
        
        # Tags de color para el log
        self.terminal.tag_config("timestamp", foreground=TEXT_DIM)
        self.terminal.tag_config("event", foreground=CYAN)
        self.terminal.tag_config("error", foreground=DANGER)

    def close_all_sections(self):
        for s in self.sections:
            if s.is_open: s.toggle()

    def log(self, message, is_error=False):
        self.terminal.config(state="normal")
        ts = datetime.now().strftime("%H:%M:%S")
        self.terminal.insert("end", f"[{ts}] ", "timestamp")
        tag = "error" if is_error else "event"
        self.terminal.insert("end", f"⚡ {message}\n", tag)
        self.terminal.config(state="disabled")
        self.terminal.see("end")

    def _logic_hw_monitor(self):
        while True:
            cpu = psutil.cpu_percent()
            ram = psutil.virtual_memory().used // (1024 * 1024)
            self.metric_cpu.config(text=f"CPU: {cpu}%")
            self.metric_ram.config(text=f"RAM: {ram}MB")
            time.sleep(2)

    def _logic_udp_listener(self):
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        try:
            sock.bind(("0.0.0.0", UDP_PORT))
            self.log(f"Escuchando telemetría en puerto {UDP_PORT}...")
            while True:
                data, addr = sock.recvfrom(2048)
                msg = data.decode("utf-8")
                self.log(f"RIDER (@{addr[0]}): {msg}")
        except: pass

if __name__ == "__main__":
    app = ThamisLabMinimalist()
    app.mainloop()
