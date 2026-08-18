import json
import os
import sys

# Importar la lógica del motor refinado
try:
    from pure_voice_refiner import ArgentumPrecisionEngineV36
except ImportError:
    print("❌ Error: No se pudo importar la lógica de pure_voice_refiner.py. Asegurate de que estén en la misma carpeta.")
    sys.exit(1)

# 1. DATASET DE TORTURA RIOPLATENSE (Simulación de ruido y soplido)
TORTURE_DATASET = [
    "che si podes de onda poneme ese tema de duque el de canistan",
    "uh la puta madre chocaron en la esquina llamá a la cana",
    "posta ruta a estacionamiento del shopping abasto rápido",
    "pasame trueno con el viza si tenés ahí",
    "pone la musica de luck ji",
    "llevame a maccdonald",
    "mandale el tema de pakistan de ysy",
    "ey guarda que hay operativo mas adelante",
    "reproducime la cancion de biza con quevedo",
    "llegar a avenida corrientes y nueve de julio",
    "poneme algo de duki duki duki",
    "tira la musica de mercedes sosa",
    "ojo que hay un choque en la autopista",
    "quiero ir al shopping abasto",
    "largame un duki de una",
    "che biza pone duki",
    "llamá a la policia que me afanaron",
    "reproducime el tema de la renga",
    "andá a la shell mas cercana",
    "pone el tema de duki el de rockstar"
]

def run_stress_test():
    print("\n==========================================================================")
    print(" 🔬 THAMIS LAB — SUITE DE STRESS SEMÁNTICO (ARGENTUM ULC)")
    print("==========================================================================")
    print(f"Inyectando {len(TORTURE_DATASET)} frases de tortura...")
    print(f"{'ITER':<4} | {'FRASE CRUDA':<40} | {'INTENT':<12} | {'PAYLOAD':<15}")
    print("-" * 80)

    engine = ArgentumPrecisionEngineV36()
    reporte_final = []

    for i, frase in enumerate(TORTURE_DATASET, 1):
        # Ejecutar NLU
        resultado = engine.resolver_contrato(frase)
        
        # Métrica de Diagnóstico (Visualización en PowerShell)
        print(f"{i:<4} | {frase[:40]:<40} | {resultado['intent']:<12} | {resultado['entity']:<15}")
        
        reporte_final.append({
            "iteration": i,
            "raw": frase,
            "nlu": resultado
        })

    # Guardar reporte blindado
    output_path = "C:/projects/LogPose4/tools/memoria_aprendizaje_test.json"
    try:
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(reporte_final, f, indent=2, ensure_ascii=False)
    except Exception as e:
        print(f"⚠️ Error guardando el reporte: {e}")

    print("==========================================================================")
    print(f"✅ Stress Test completado. Reporte guardado en: {output_path}")

if __name__ == "__main__":
    run_stress_test()
