import socket
import time
import json
import threading

# --- CONFIGURACIÓN DE PUERTOS STAFF ---
PC_LISTEN_PORT = 5055    # La PC escucha telemetría aquí
APP_DISCOVERY_PORT = 5052 # El celu busca a la PC aquí
MAGIC_TOKEN = "LOGPOSE_BRIDGE_HERE"

class OpenHandsBridge:
    def __init__(self):
        self.last_telemetry = {}
        self.running = True

    def start_discovery(self):
        """ Envía el latido de descubrimiento para que el celu encuentre la PC """
        print(f"📡 [Discovery] Iniciando broadcast en puerto {APP_DISCOVERY_PORT}...")
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
        try:
            while self.running:
                # Usamos broadcast universal para evitar bloqueos de interfaz
                sock.sendto(MAGIC_TOKEN.encode(), ('255.255.255.255', APP_DISCOVERY_PORT))
                time.sleep(5)
        except Exception as e:
            print(f"❌ Error en Discovery: {e}")
        finally:
            sock.close()

    def start_listener(self):
        """ Escucha los JSON de telemetría que manda THAMIS """
        print(f"👂 [Listener] Escuchando telemetría en puerto {PC_LISTEN_PORT}...")
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        try:
            sock.bind(('0.0.0.0', PC_LISTEN_PORT))
        except Exception as e:
            print(f"❌ No se pudo abrir el puerto {PC_LISTEN_PORT}: {e}")
            return
        
        while self.running:
            try:
                data, addr = sock.recvfrom(8192)
                payload = json.loads(data.decode())
                self.process_for_openhands(payload, addr)
            except Exception as e:
                pass # Ignorar paquetes malformados

    def process_for_openhands(self, payload, addr):
        """ Formatea los datos para el Canvas de OpenHands """
        self.last_telemetry = payload
        
        event_summary = {
            "intent": payload.get("detected_intent", "UNKNOWN"),
            "text": payload.get("final_clean_text", ""),
            "latency": f"{payload.get('total_latency_ms', 0)}ms",
            "timestamp": time.strftime("%H:%M:%S"),
            "device_ip": addr[0]
        }
        
        print(f"\n🚀 [EVENT] {event_summary['timestamp']} | {event_summary['intent']} | '{event_summary['text']}'")
        
        # Escribimos al log que OpenHands debe vigilar
        try:
            with open("openhands_telemetry.log", "a", encoding="utf-8") as f:
                f.write(json.dumps(event_summary, ensure_ascii=False) + "\n")
        except:
            pass

if __name__ == "__main__":
    bridge = OpenHandsBridge()
    
    # Hilo de Descubrimiento
    t1 = threading.Thread(target=bridge.start_discovery, daemon=True)
    t1.start()
    
    # Hilo principal: Listener
    try:
        bridge.start_listener()
    except KeyboardInterrupt:
        print("\n🛑 Apagando Bridge de OpenHands...")
        bridge.running = False
