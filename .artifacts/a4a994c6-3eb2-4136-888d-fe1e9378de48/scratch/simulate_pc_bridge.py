import socket
import time
import json
import threading

# Configuración de puertos
DISCOVERY_PORT = 5051
COMMAND_PORT = 5055
MAGIC_TOKEN = "LOGPOSE_BRIDGE_HERE"

def start_discovery_broadcast():
    """Envía el token de descubrimiento en bucle."""
    print(f"📡 Iniciando broadcast de descubrimiento en puerto {DISCOVERY_PORT}...")
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)

    try:
        while True:
            # 255.255.255.255 es más confiable en Windows que '<broadcast>'
            sock.sendto(MAGIC_TOKEN.encode(), ('255.255.255.255', DISCOVERY_PORT))
            print("📤 Latido de descubrimiento enviado (Heartbeat)...")
            time.sleep(5)
    except KeyboardInterrupt:
        print("\n🛑 Deteniendo descubrimiento...")
    except Exception as e:
        print(f"❌ Error en descubrimiento: {e}")
    finally:
        sock.close()

def start_command_listener():
    """Escucha comandos enviados por el celular."""
    print(f"👂 Escuchando comandos en puerto {COMMAND_PORT}...")
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    
    # Intentar permitir reutilización de dirección/puerto
    try:
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.bind(('0.0.0.0', COMMAND_PORT))
    except Exception as e:
        print(f"❌ No se pudo abrir el puerto {COMMAND_PORT}: {e}")
        print("💡 TIP: Ejecutá 'taskkill /F /IM python.exe /T' y volvé a intentar.")
        return

    try:
        while True:
            data, addr = sock.recvfrom(4096)
            print(f"\n📥 Recibido desde {addr}:")
            try:
                payload = json.loads(data.decode())
                print(f"✅ Comando: {json.dumps(payload, indent=2, ensure_ascii=False)}")
            except:
                print(f"📝 Raw: {data.decode()}")
    except KeyboardInterrupt:
        print("\n🛑 Deteniendo listener...")
    finally:
        sock.close()

if __name__ == "__main__":
    # Listener en segundo plano
    listener_thread = threading.Thread(target=start_command_listener, daemon=True)
    listener_thread.start()
    
    # Discovery en hilo principal
    start_discovery_broadcast()
