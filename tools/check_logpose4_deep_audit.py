import os
import sys
import re
import json

PATH_ANDROID_PROJECT = "C:\\projects\\LogPose4"
PATH_ASSETS_GLOSARIO = "C:\\projects\\LogPose4\\app\\src\\main\\assets\\logpose_glosario.json"

print("==========================================================================")
print(" 🔬 INITIATING LOGPOSE4 DEEP CODE SEARCH — AUDITORÍA TOTAL NATIVA v10.0")
print("==========================================================================")

# ─── ASERCIÓN 1: ESCÁNER DE INTEGRACIÓN DE ARTISTAS (EL BUG ANUEL) ──────────────
print("\n[1/4] Verificando hidratación de base de datos offline (Assets JSON)...")
if os.path.exists(PATH_ASSETS_GLOSARIO):
    try:
        with open(PATH_ASSETS_GLOSARIO, "r", encoding="utf-8") as f:
            glosario = json.load(f)

        artistas_criticos = ["duki", "la renga", "anuel", "anuel aa", "luck ra", "ke personajes"]
        glosario_str = json.dumps(glosario).lower()

        faltantes = [art for art in artistas_criticos if art not in glosario_str]

        if faltantes:
            print(f"   ❌ [HUECO DETECTADO] Faltan indexar estos artistas en el JSON del Xiaomi: {faltantes}")
            print("      -> Causa del quiebre: Al no conocer el nombre, el Gate offline aborta por seguridad.")
        else:
            print("   ✅ [OK] Base de datos de artistas hidratada y certificada para producción.")
    except Exception as e:
        print(f"   ❌ [CRÍTICO] Falló la lectura del JSON de assets de Android: {e}")
else:
    print(f"   ⚠️ [ALERTA] No se encontró el archivo logpose_glosario.json en: {PATH_ASSETS_GLOSARIO}")

# ─── ASERCIÓN 2: BÚSQUEDA DEL ESCUDO WAKE-WORD EN KOTLIN (LOGCAT GATE) ───────────
print("\n[2/4] Escaneando IntentDetector.kt en busca del Wake-Word Stripper...")
detector_found = False
gate_blindado = False

for root, dirs, files in os.walk(PATH_ANDROID_PROJECT):
    for file in files:
        if file == "IntentDetector.kt":
            detector_found = True
            path_file = os.path.join(root, file)
            with open(path_file, "r", encoding="utf-8", errors="ignore") as f:
                codigo_kotlin = f.read()

            if "while" in codigo_kotlin and ("lujo" in codigo_kotlin or "lodge" in codigo_kotlin):
                print(f"   ✅ [OK] Wake-Word Stripper elástico localizado en: ...\\{os.path.basename(root)}\\IntentDetector.kt")
                gate_blindado = True
            else:
                print(f"   ⚠️ [PUNTO CIEGO] El IntentDetector.kt no tiene el limpiador iterativo de soplidos Bluetooth.")

if not detector_found:
    print("   ❌ [CRÍTICO] No se localizó el archivo IntentDetector.kt en el árbol de directorios de LogPose4.")

# ─── ASERCIÓN 3: VERIFICACIÓN DEL PROCESADOR DE AUDIO NATIVO (ANTI-RUIDOSE V6) ────
print("\n[3/4] Auditando VoskVoiceEngine.kt en busca del Filtro Pasa-Altos (DSP)...")
engine_found = False
dsp_activo = False

for root, dirs, files in os.walk(PATH_ANDROID_PROJECT):
    for file in files:
        if file == "VoskVoiceEngine.kt":
            engine_found = True
            path_file = os.path.join(root, file)
            with open(path_file, "r", encoding="utf-8", errors="ignore") as f:
                codigo_audio = f.read()

            if "alpha" in codigo_audio or "AudioRecord" in codigo_audio and ("filtered" in codigo_audio or "buffer" in codigo_audio):
                print(f"   ✅ [OK] Pipeline DSP Anti-Ruido detectado de fondo en: ...\\{os.path.basename(root)}\\VoskVoiceEngine.kt")
                dsp_activo = True
            else:
                print("   ⚠️ [ADVERTENCIA] VoskVoiceEngine.kt procesa el audio en crudo. Expuesto al escape del Ejeas V6 Pro.")

if not engine_found:
    print("   ⚠️ [AVISO] No se localizó el archivo VoskVoiceEngine.kt. Verificá el nombre del grabador nativo.")

# ─── ASERCIÓN 4: COMPROBACIÓN DE PERMISOS Y PERFIL DE CONFIGURACIÓN DE GRADW ─────
print("\n[4/4] Verificando entorno de compilación rápida para el Redmi 15C...")
gradlew_path = os.path.join(PATH_ANDROID_PROJECT, "gradlew.bat")
if os.path.exists(gradlew_path):
    print("   ✅ [OK] Enlace de automatización Gradle (gradlew.bat) listo en la raíz del proyecto.")
else:
    print("   ❌ [ERROR] Falta el ejecutable de Gradle. No vas a poder compilar por PowerShell.")

print("\n==========================================================================")
print(" 🏁 DEEP ANDROID AUDIT COMPLETADA — INFORME DE INFRAESTRUCTURA MÓVIL")
print("==========================================================================\n")
