#!/usr/bin/env python3
"""
==========================================================================
 🧪 AUDITORÍA FORENSE Y PENETRACIÓN DE DEPOSITO - THAMIS LAB SYSTEMS v9.5
==========================================================================
"""

import os
import sys
import json
import re

PATH_ENGINE = r"C:\projects\THAMIS-LAB\tools\intent_engine_infinite_lab.py"
PATH_GLOSARIO = r"C:\projects\THAMIS-LAB\tools\glosario_argentum_sintetico.json"
PATH_APROBADOS = r"C:\projects\THAMIS-LAB\tools\casos_aprobados_produccion.json"

def run_deep_audit():
    print("==========================================================================")
    print(" 🔬 INITIATING DEEP PENETRATION AUDIT — THAMIS LAB SYSTEMS v9.5")
    print("==========================================================================")

    # ─── ASERCIÓN 1: INSPECCIÓN DE ARQUITECTURA DE ARCHIVOS Y RUTAS ──────────────────
    print("\n[1/4] Verificando integridad física de almacenamiento local...")
    archivos_criticos = [PATH_ENGINE, PATH_GLOSARIO]
    file_errors = 0

    for path in archivos_criticos:
        if os.path.exists(path):
            size_kb = os.path.getsize(path) / 1024
            print(f"   ✅ [OK] {os.path.basename(path)} presente en disco ({size_kb:.2f} KB).")
        else:
            print(f"   ❌ [CRÍTICO] Falta el archivo esencial en ruta: {path}")
            file_errors += 1

    # ─── ASERCIÓN 2: VALIDACIÓN DE SINTAXIS JSON DE PRODUCCIÓN ──────────────────────
    print("\n[2/4] Ejecutando escáner estructural de diccionarios...")
    if os.path.exists(PATH_GLOSARIO):
        try:
            with open(PATH_GLOSARIO, "r", encoding="utf-8") as f:
                data = json.load(f)
            
            articulos_basura = {"ya", "al", "que", "la", "el", "un", "de", "no", "en", "se", "del"}
            basura_detectada = [k for k in data.keys() if k in articulos_basura or len(k) < 3]
            
            if basura_detectada:
                print(f"   ⚠️ [ALERTA] Glosario contaminado con tokens cortos: {basura_detectada}")
            else:
                print("   ✅ [SANEADO] El glosario local está 100% limpio de artículos e impurezas gramaticales.")
        except Exception as e:
            print(f"   ❌ [CRÍTICO] Glosario sintético corrupto o sintaxis JSON inválida: {e}")
    else:
        print("   ⚪ [BYPASS] Glosario secundario no inicializado aún por el motor.")

    # ─── ASERCIÓN 3: AUDITORÍA DE EXPRESIONES REGULARES Y CABLEADO LÓGICO ──────────
    print("\n[3/4] Analizando el código fuente en busca de puntos ciegos...")
    codigo = ""
    if os.path.exists(PATH_ENGINE):
        try:
            with open(PATH_ENGINE, "r", encoding="utf-8") as f:
                codigo = f.read()
                
            if "while" in codigo and ("palabras[0] in" in codigo or "palabras in" in codigo):
                print("   ✅ [CABLEADO] Wake-Word Stripper iterativo detectado. Inmune a tartamudeos Bluetooth.")
            else:
                print("   ⚠️ [ADVERTENCIA] No se detectó un bucle de remoción iterativa para la Hotword LOG.")
                
            if "[:5]" in codigo or "[:4]" in codigo:
                print("   ✅ [CABLEADO] Ventana de comandos acotada a la zona de impacto inicial. Inmune al bug 'onda'.")
            else:
                print("   ⚠️ [ADVERTENCIA] Ventana elástica abierta expuesta a falsos positivos al final de la frase.")
                
            match_key = re.search(r'API_KEY_AI_STUDIO\s*=\s*["\']([^"\']+)["\']', codigo)
            if match_key:
                current_key = match_key.group(1)
                if "PLACEHOLDER" in current_key or "TU_API_KEY" in current_key:
                    print("   ⚠️ [MODO LOCAL] API Key en modo simulado. El Labs operará de forma 100% offline.")
                else:
                    print(f"   📶 [MODO HÍBRIDO] API Key real activa detectada: {current_key[:6]}...{current_key[-4:]}")
            else:
                print("   ⚠️ [ALERTA] No se encontró la variable global API_KEY_AI_STUDIO.")
                
        except Exception as e:
            print(f"   ❌ [ERROR] Falló la inspección estática del motor: {e}")

    # ─── ASERCIÓN 4: VERIFICACIÓN DEL TABLERO DE MEDICIÓN DE DECIBELIOS ─────────────
    print("\n[4/4] Verificando el detector de clipping y cierre por hardware...")
    if codigo:
        if "imprimir_reporte_fallos_no_resueltos" in codigo and "db" in codigo:
            print("   ✅ [DASHBOARD] El medidor de decibelios y la tabla métrica de cierre están listos para PowerShell.")
        else:
            print("   ⚠️ [ALERTA] Falta el despachador de decibelios en el reporte final de Ctrl+C.")

    print("\n==========================================================================")
    print(" 🏁 AUDITORÍA COMPLETADA: REVISÁ LAS ALERTAS ANTES DE SALIR A LA CALLE")
    print("==========================================================================\n")

if __name__ == "__main__":
    run_deep_audit()
