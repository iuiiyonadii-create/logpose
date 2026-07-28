import socket
import json
import tkinter as tk
from tkinter import ttk, scrolledtext, messagebox, simpledialog
import threading
import requests
import os
import math
import random
import time
import subprocess
import psutil
from io import BytesIO
from PIL import Image, ImageTk
from datetime import datetime

# CONFIGURACIÓN
UDP_PORT = 9999
BRAIN_URL = "http://localhost:5000"

class ThamisLabApp:
    def __init__(self, root):
        self.root = root
        self.root.title("THAMIS MASTER COMMAND v25.0 - Autonomous Engineering Center")
        self.root.geometry("1750x1050")
        self.root.configure(bg="#010101")

        self.is_lab_active = False
        self.local_ip = self.get_ip()

        self.setup_ui()
        threading.Thread(target=self.hw_monitor, daemon=True).start()
        threading.Thread(target=self.start_udp_server, daemon=True).start()

    def get_ip(self):
        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            s.connect(("8.8.8.8", 80))
            return s.getsockname()[0]
        except: return "127.0.0.1"

    def setup_ui(self):
        # --- HEADER ---
        top = tk.Frame(self.root, bg="#111", height=100)
        top.pack(fill="x")
        tk.Label(top, text="THAMIS MISSION CONTROL", fg="#00cec9", bg="#111", font=("Segoe UI", 32, "bold")).pack(side="left", padx=40)

        self.hw_panel = tk.Label(top, text="CPU: 0% | RAM: 0% | AUTO-ENGINEERING: ON", fg="#fdcb6e", bg="#111", font=("Consolas", 14))
        self.hw_panel.pack(side="right", padx=40)

        self.btn_power = tk.Button(top, text="▶ START ORCHESTRATOR", bg="#6c5ce7", fg="white", font=("Segoe UI", 12, "bold"), width=25, height=2, command=self.toggle_lab)
        self.btn_power.pack(side="right", padx=20)

        # --- TABS ---
        self.nb = ttk.Notebook(self.root)
        self.nb.pack(fill="both", expand=True, padx=15, pady=15)

        self.tab_dash = tk.Frame(self.nb, bg="#050505")
        self.tab_security = tk.Frame(self.nb, bg="#050505")
        self.tab_docs = tk.Frame(self.nb, bg="#050505")
        self.tab_vision = tk.Frame(self.nb, bg="#050505")

        self.nb.add(self.tab_dash, text=" 📊 ORCHESTRATOR DASHBOARD ")
        self.nb.add(self.tab_security, text=" 🛡️ SECURITY ARCHITECT ")
        self.nb.add(self.tab_docs, text=" 📖 AUTO DOCUMENTATION ")
        self.nb.add(self.tab_vision, text=" 📜 MASTER SPEC v25.0 ")

        self.setup_dash_tab()
        self.setup_security_tab()
        self.setup_docs_tab()
        self.setup_vision_tab()

    def setup_dash_tab(self):
        paned = tk.PanedWindow(self.tab_dash, orient="horizontal", bg="#050505", borderwidth=0)
        paned.pack(fill="both", expand=True, padx=10, pady=10)

        # Log Central de la Misión
        left = tk.Frame(paned, bg="#0f0f0f", width=800)
        paned.add(left)
        tk.Label(left, text="MISSION CONTROL LOG (TAO)", fg="#00cec9", bg="#0f0f0f", font=("Segoe UI", 14, "bold")).pack(pady=15)
        self.mission_log = scrolledtext.ScrolledText(left, bg="#000", fg="#55efc4", font=("Consolas", 12))
        self.mission_log.pack(fill="both", expand=True, padx=20, pady=10)

        # Resumen de Agentes
        right = tk.Frame(paned, bg="#050505")
        paned.add(right)
        tk.Label(right, text="AGENT DISPATCHER", fg="#6c5ce7", bg="#050505", font=("Segoe UI", 12, "bold")).pack(pady=15)
        self.agent_tree = ttk.Treeview(right, columns=("ID", "STATUS"), show="headings", height=10)
        self.agent_tree.heading("ID", text="AGENTE"); self.agent_tree.heading("STATUS", text="ESTADO")
        self.agent_tree.pack(fill="x", padx=15)
        for a in ["Voice", "Chaos", "Security", "Performance", "Documentation"]:
            self.agent_tree.insert("", "end", iid=a, values=(a, "Standby"))

    def setup_security_tab(self):
        frame = tk.Frame(self.tab_security, bg="#050505", padx=40, pady=30)
        frame.pack(fill="both", expand=True)
        tk.Label(frame, text="TASA: SECURITY & PRIVACY AUDIT", fg="#ff7675", bg="#050505", font=("Segoe UI", 24, "bold")).pack(anchor="w")

        self.sec_box = scrolledtext.ScrolledText(frame, bg="#000", fg="#ff7675", font=("Consolas", 12))
        self.sec_box.pack(fill="both", expand=True, pady=20)

        tk.Button(frame, text="🛡️ EJECUTAR ANÁLISIS DE SEGURIDAD", bg="#ff7675", fg="black", font=("Segoe UI", 11, "bold"), height=2, command=self.run_security_audit).pack(fill="x")

    def setup_docs_tab(self):
        frame = tk.Frame(self.tab_docs, bg="#050505", padx=40, pady=30)
        frame.pack(fill="both", expand=True)
        tk.Label(frame, text="THAMIS DOCUMENTATION ENGINE", fg="#55efc4", bg="#050505", font=("Segoe UI", 24, "bold")).pack(anchor="w")

        btn_frame = tk.Frame(frame, bg="#050505")
        btn_frame.pack(fill="x", pady=20)
        tk.Button(btn_frame, text="📄 GENERAR ADR (Decisión Técnica)", bg="#00b894", fg="white", command=lambda: self.generate_doc("ADR")).pack(side="left", padx=10)
        tk.Button(btn_frame, text="📋 GENERAR REPORTE DIARIO", bg="#00b894", fg="white", command=lambda: self.generate_doc("DAILY")).pack(side="left", padx=10)

        self.doc_viewer = scrolledtext.ScrolledText(frame, bg="#111", fg="white", font=("Consolas", 12))
        self.doc_viewer.pack(fill="both", expand=True)

    def setup_vision_tab(self):
        self.vision_view = scrolledtext.ScrolledText(self.tab_vision, bg="#000", fg="#aaa", font=("Consolas", 11), padx=20, pady=20)
        self.vision_view.pack(fill="both", expand=True)
        try:
            with open("THAMIS_MASTER_SPEC.md", "r", encoding="utf-8") as f:
                self.vision_view.insert(tk.END, f.read())
        except: pass

    def toggle_lab(self):
        self.is_lab_active = not self.is_lab_active
        self.btn_power.config(text="■ STOP ORCHESTRATOR" if self.is_lab_active else "▶ START ORCHESTRATOR", bg="#d63031" if self.is_lab_active else "#6c5ce7")
        if self.is_lab_active: self.run_orchestrator_loop()

    def run_orchestrator_loop(self):
        self.mission_log.insert(tk.END, f"> [{datetime.now().strftime('%H:%M:%S')}] Iniciando ciclo autónomo...\n")
        def call():
            try:
                resp = requests.post(f"{BRAIN_URL}/taes/orchestrator/dispatch", json={"task": "Daily Audit"})
                if resp.status_code == 200:
                    plan = resp.json()['plan']
                    for step in plan:
                        time.sleep(1)
                        self.root.after(0, lambda s=step: self.mission_log.insert(tk.END, f"📍 Ejecutando: {s}...\n"))
            except: pass
        threading.Thread(target=call).start()

    def run_security_audit(self):
        self.sec_box.insert(tk.END, "> Iniciando Auditoría TASA v25.0...\n")
        def call():
            try:
                resp = requests.post(f"{BRAIN_URL}/taes/security/audit", json={})
                if resp.status_code == 200:
                    data = resp.json()
                    res = f"\n✅ SECURITY SCORE: {data['score']}/100\nSTATUS: {data['status']}\n"
                    self.root.after(0, lambda: self.sec_box.insert(tk.END, res, "bold"))
                    for f in data['findings']: self.root.after(0, lambda msg=f: self.sec_box.insert(tk.END, f"  📍 {msg}\n"))
            except: pass
        threading.Thread(target=call).start()

    def generate_doc(self, dtype):
        def call():
            try:
                resp = requests.post(f"{BRAIN_URL}/taes/docs/generate", json={"type": dtype})
                if resp.status_code == 200:
                    data = resp.json()
                    self.root.after(0, lambda: self.doc_viewer.insert(tk.END, f"\n--- {data['filename']} ---\n{data['content']}\n"))
            except: pass
        threading.Thread(target=call).start()

    def hw_monitor(self):
        while True:
            cpu = psutil.cpu_percent(); ram = psutil.virtual_memory().percent
            self.hw_panel.config(text=f"CPU: {cpu}% | RAM: {ram}% | AUTO-ENGINEERING: ON")
            time.sleep(1)

    def start_udp_server(self):
        with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as s:
            s.bind(("0.0.0.0", UDP_PORT))
            while True:
                data, addr = s.recvfrom(2048)
                print(f"RIDER: {data.decode('utf-8')}")

if __name__ == "__main__":
    root = tk.Tk()
    app = ThamisLabApp(root)
    root.mainloop()
