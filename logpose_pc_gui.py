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
        self.root.title("THAMIS MASTER COMMAND v30.0 - Distributed Engineering Lab")
        self.root.geometry("1750x1050")
        self.root.configure(bg="#010101")

        self.is_lab_active = False
        self.local_ip = self.get_ip()

        self.setup_ui()
        threading.Thread(target=self.hw_monitor, daemon=True).start()
        threading.Thread(target=self.cloud_sync_loop, daemon=True).start()

    def get_ip(self):
        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            s.connect(("8.8.8.8", 80))
            return s.getsockname()[0]
        except: return "127.0.0.1"

    def setup_ui(self):
        # --- TOP HEADER: MISSION CONTROL ---
        top = tk.Frame(self.root, bg="#111", height=100)
        top.pack(fill="x")
        tk.Label(top, text="THAMIS MASTER COMMAND", fg="#00cec9", bg="#111", font=("Segoe UI", 32, "bold")).pack(side="left", padx=40)

        # STATUS PANEL
        self.cloud_label = tk.Label(top, text="CLOUD NODES: 0 | JOBS: 0", fg="#fdcb6e", bg="#111", font=("Consolas", 14))
        self.cloud_label.pack(side="right", padx=40)

        self.btn_power = tk.Button(top, text="▶ START CLOUD ENGINE", bg="#6c5ce7", fg="white", font=("Segoe UI", 12, "bold"), width=25, height=2, command=self.toggle_lab)
        self.btn_power.pack(side="right", padx=20)

        # --- MAIN TABS ---
        self.nb = ttk.Notebook(self.root)
        self.nb.pack(fill="both", expand=True, padx=15, pady=15)

        self.tab_cloud = tk.Frame(self.nb, bg="#050505")
        self.tab_arch = tk.Frame(self.nb, bg="#050505")
        self.tab_vision = tk.Frame(self.nb, bg="#050505")

        self.nb.add(self.tab_cloud, text=" ☁️ DISTRIBUTED NODES ")
        self.nb.add(self.tab_arch, text=" 🏗️ ARCHITECTURE ")
        self.nb.add(self.tab_vision, text=" 📖 MASTER SPEC v30.0 ")

        self.setup_cloud_tab()
        self.setup_arch_tab()
        self.setup_vision_tab()

    def setup_cloud_tab(self):
        paned = tk.PanedWindow(self.tab_cloud, orient="horizontal", bg="#050505", borderwidth=0)
        paned.pack(fill="both", expand=True, padx=10, pady=10)

        # Mapa de Nodos
        left = tk.Frame(paned, bg="#0f0f0f", width=700)
        paned.add(left)
        tk.Label(left, text="LABORATORIO DISTRIBUIDO", fg="#00cec9", bg="#0f0f0f", font=("Segoe UI", 14, "bold")).pack(pady=15)

        self.node_tree = ttk.Treeview(left, columns=("ID", "TYPE", "CPU", "STATUS"), show="headings", height=15)
        self.node_tree.heading("ID", text="NODO_ID"); self.node_tree.heading("TYPE", text="TIPO"); self.node_tree.heading("CPU", text="USO%"); self.node_tree.heading("STATUS", text="ESTADO")
        self.node_tree.pack(fill="both", expand=True, padx=20)

        tk.Button(left, text="➕ AGREGAR NODO (SCALE)", bg="#00b894", fg="white", font=("Segoe UI", 11, "bold"), height=2, command=self.scale_cloud).pack(fill="x", padx=20, pady=20)

        # Consola Central
        right = tk.Frame(paned, bg="#050505")
        paned.add(right)
        tk.Label(right, text="CENTRAL JOB DISPATCHER", fg="#6c5ce7", bg="#050505", font=("Segoe UI", 12, "bold")).pack(anchor="w", padx=15)
        self.cloud_log = scrolledtext.ScrolledText(right, bg="#000", fg="#55efc4", font=("Consolas", 11))
        self.cloud_log.pack(fill="both", expand=True, padx=10, pady=10)

    def setup_arch_tab(self):
        # Visor de Arquitectura y ADRs
        frame = tk.Frame(self.tab_arch, bg="#050505", padx=40, pady=30)
        frame.pack(fill="both", expand=True)
        tk.Label(frame, text="MASTER ARCHITECTURE DESIGN", fg="#00cec9", bg="#050505", font=("Segoe UI", 24, "bold")).pack(anchor="w")

        self.arch_view = scrolledtext.ScrolledText(frame, bg="#111", fg="white", font=("Consolas", 12))
        self.arch_view.pack(fill="both", expand=True, pady=20)
        try:
            with open("ARCHITECTURE.md", "r", encoding="utf-8") as f:
                self.arch_view.insert(tk.END, f.read())
        except: pass

        tk.Button(frame, text="📝 REGISTRAR DECISIÓN TÉCNICA (ADR)", bg="#6c5ce7", fg="white", command=self.create_adr).pack()

    def setup_vision_tab(self):
        self.vision_view = scrolledtext.ScrolledText(self.tab_vision, bg="#000", fg="#aaa", font=("Consolas", 11), padx=20, pady=20)
        self.vision_view.pack(fill="both", expand=True)
        try:
            with open("THAMIS_MASTER_SPEC.md", "r", encoding="utf-8") as f:
                self.vision_view.insert(tk.END, f.read())
        except: pass

    def toggle_lab(self):
        self.is_lab_active = not self.is_lab_active
        self.btn_power.config(text="■ STOP CLOUD" if self.is_lab_active else "▶ START CLOUD ENGINE", bg="#d63031" if self.is_lab_active else "#6c5ce7")

    def scale_cloud(self):
        def call():
            try:
                resp = requests.post(f"{BRAIN_URL}/taes/cloud/scale")
                if resp.status_code == 200:
                    self.root.after(0, lambda: messagebox.showinfo("Cloud Scaling", resp.json()['msg']))
            except: pass
        threading.Thread(target=call).start()

    def create_adr(self):
        decision = simpledialog.askstring("Nuevo ADR", "Describe la decisión arquitectónica:")
        if not decision: return
        def call():
            try:
                resp = requests.post(f"{BRAIN_URL}/taes/architecture/adr", json={"decision": decision})
                if resp.status_code == 200:
                    msg = resp.json()['content']
                    self.root.after(0, lambda: self.cloud_log.insert(tk.END, f"✅ ADR CREADO: {msg}\n"))
            except: pass
        threading.Thread(target=call).start()

    def cloud_sync_loop(self):
        while True:
            try:
                resp = requests.get(f"{BRAIN_URL}/taes/cloud/status")
                if resp.status_code == 200:
                    data = resp.json()
                    self.root.after(0, lambda d=data: self.update_cloud_ui(d))
            except: pass
            time.sleep(5)

    def update_cloud_ui(self, data):
        self.cloud_label.config(text=f"CLOUD NODES: {data['active_nodes']} | JOBS: {sum(data['job_queue'].values())}")
        self.node_tree.delete(*self.node_tree.get_children())
        for n in data['nodes']:
            self.node_tree.insert("", "end", values=(n['id'], n['type'], f"{n['cpu']}%", n['status']))

    def hw_monitor(self):
        while True:
            cpu = psutil.cpu_percent(); ram = psutil.virtual_memory().percent
            # En v30 el master monitoriza su propia salud
            time.sleep(1)

if __name__ == "__main__":
    root = tk.Tk()
    app = ThamisLabApp(root)
    root.mainloop()
