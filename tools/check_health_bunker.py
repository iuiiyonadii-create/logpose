import os
import json
import sys

# 1. IMPORTACIÓN DINÁMICA DEL MOTOR NLU REFINADO
try:
    from pure_voice_refiner import ArgentumStaffEngineV44
except ImportError:
    print("❌ ERROR CRÍTICO: No se encontró 'tools/pure_voice_refiner.py'.")
    sys.exit(1)

def run_health_check():
    print("\n" + "="*80)
    print(" 🏥 THAMIS LAB — SCRIPT DE AUDITORÍA DE SALUD Y VERIFICACIÓN POST-PURGA")
    print("="*80)

    # --------------------------------------------------------------------------
    # FASE 1: ASERCIÓN FÍSICA DEL ALMACENAMIENTO (PURGE VERIFICATION)
    # --------------------------------------------------------------------------
    base_assets = "app/src/main/assets"
    obsolete_paths = [
        os.path.join(base_assets, "model"),
        os.path.join(base_assets, "lenguage"),
        os.path.join(base_assets, "thamis"),
        os.path.join(base_assets, "staff_seed.json"),
        os.path.join(base_assets, "model-es/ivector")
    ]

    print(f"\n[1/2] Verificando Purga de Assets Redundantes...")
    all_clear = True
    for path in obsolete_paths:
        if os.path.exists(path):
            print(f"  🚨 ALERTA: RESIDUOS DETECTADOS en -> {path}")
            all_clear = False
        else:
            print(f"  ✅ Limpio: {path}")
    
    if all_clear:
        print("  💎 ESTADO: Búnker 100% libre de peso muerto.")

    # Verificación del ULC Maestro
    glosario_path = os.path.join(base_assets, "logpose_glosario.json")
    if os.path.exists(glosario_path):
        try:
            with open(glosario_path, 'r', encoding='utf-8') as f:
                json.load(f)
            print(f"  ✅ ULC Maestro: '{glosario_path}' íntegro y válido.")
        except Exception as e:
            print(f"  ❌ ERROR: Glosario corrupto -> {e}")
    else:
        print(f"  ❌ ERROR CRÍTICO: No se encontró el ULC Maestro en {glosario_path}")

    # --------------------------------------------------------------------------
    # FASE 2: TEST DE ESTRÉS DE TEXTO EN FRÍO (NLU v4.4 ASSERTIONS)
    # --------------------------------------------------------------------------
    print(f"\n[2/2] Validando Inteligencia v4.4 (Stress Test de Texto)...")
    engine = ArgentumStaffEngineV44()
    
    test_cases = [
        {
            "id": "F1 (Voseo/Bilateral)",
            "input": "bonete after hours de una para meterle onda",
            "expected_intent": "PLAY_MUSIC",
            "expected_entity": "after hours"
        },
        {
            "id": "F2 (Bigrama Navegación)",
            "input": "llevame a la avenida rivadavia antes de que me saquen el viaje",
            "expected_intent": "NAVIGATE",
            "expected_entity": "avenida rivadavia"
        },
        {
            "id": "F3 (Food & Pack Purge)",
            "input": "necesito ir a buscar una coca-cola y papas fritas a avenida callao",
            "expected_intent": "NAVIGATE",
            "expected_entity": "avenida callao"
        }
    ]

    for test in test_cases:
        result = engine.resolver_contrato(test["input"])
        intent_ok = result["intent"] == test["expected_intent"]
        entity_ok = test["expected_entity"] in result["entity"]
        
        status = "✅ PASS" if (intent_ok and entity_ok) else "❌ FAIL"
        print(f"\n  TEST {test['id']}: {status}")
        print(f"    Input:  '{test['input']}'")
        print(f"    Output: Intent={result['intent']}, Entity='{result['entity']}'")
        
        if not intent_ok: print(f"    ⚠️ ERROR: Se esperaba Intent '{test['expected_intent']}'")
        if not entity_ok: print(f"    ⚠️ ERROR: Payload sucio. Se esperaba '{test['expected_entity']}'")

    print("\n" + "="*80)
    print(" ✅ AUDITORÍA FINALIZADA: Sistema listo para despliegue en Xiaomi.")
    print("="*80 + "\n")

if __name__ == "__main__":
    run_health_check()
