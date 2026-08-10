import subprocess
import sys
import re

print("======================================================================")
print(" 🗣️ TELEPROMPTER DE VOZ EN TIEMPO REAL (LOGPOSE)")
print("======================================================================")
print("Habla por el casco...\n")

cmd = ["adb", "logcat", "-v", "time", "-s", "Vosk", "HPTranscriber", "Pipeline", "IntentDetector", "Dispatcher", "Gate"]

try:
    process = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True, encoding="utf-8", errors="ignore")

    for line in iter(process.stdout.readline, ''):
        line = line.strip()
        
        if "Sherpa local resolvió ->" in line:
            match = re.search(r"Sherpa local resolvió -> '(.*)'", line)
            if match:
                print(f"\n🗣️ DIJISTE: '{match.group(1)}'")
                sys.stdout.flush()

        elif "Vosk: Centinela detectó ->" in line:
            match = re.search(r"Vosk: Centinela detectó -> '(.*)'", line)
            if match:
                print(f"👂 OYÓ CENTINELA: '{match.group(1)}'")
                sys.stdout.flush()

        elif "Dispatcher: Reproduciendo música:" in line:
            match = re.search(r"Dispatcher: Reproduciendo música: '(.*)'", line)
            if match:
                print(f"🎵 ACCIÓN: Reproduciendo '{match.group(1)}' en Spotify")
                sys.stdout.flush()

        elif "Dispatcher:" in line:
            match = re.search(r"Dispatcher: (.*)", line)
            if match:
                print(f"🚀 ACCIÓN: {match.group(1)}")
                sys.stdout.flush()

        elif "Gate: Bloqueado -" in line:
            match = re.search(r"Gate: Bloqueado - (.*)", line)
            if match:
                print(f"⚠️ NO ENTENDIDO / IGNORADO: {match.group(1)}")
                sys.stdout.flush()
except KeyboardInterrupt:
    print("\nMonitor detenido.")
    sys.exit(0)
