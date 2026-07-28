# THAMIS KNOWLEDGE GRAPH

## Objetivo
Transformar el conocimiento de ingeniería de THAMIS en una red estructurada capaz de reutilizar experiencia, detectar relaciones entre tecnologías y aprender patrones de diseño.

## Componentes del Grafo
- **Nodos (`GraphNode`):** Representan entidades como Proyectos, Módulos, Tecnologías y Patrones.
- **Relaciones (`GraphEdge`):** Definen cómo se conectan los nodos (ej: `USES`, `DEPENDS_ON`, `IMPLEMENTS`).

## Ontologías
- **Tecnología:** Clasificación de lenguajes, frameworks y bases de datos.
- **Arquitectura:** Relaciones entre Clean Architecture, MVVM, Hexagonal, etc.
- **Patrones:** Catálogo de patrones de diseño (Repository, Observer, etc.).

## Motores de Inteligencia
- **Relationship Engine:** Infiere relaciones automáticamente entre componentes.
- **Knowledge Reasoner:** Sugiere soluciones basadas en combinaciones de tecnología y requisitos.
- **Similarity Engine:** Encuentra proyectos o módulos similares para reutilización.
- **Recommendation Engine:** Propone las mejores prácticas y stacks según el contexto.

## Flujo de Conocimiento
1. **Indexación:** El código y las decisiones se transforman en nodos del grafo.
2. **Inferencia:** El sistema detecta dependencias y patrones implícitos.
3. **Consulta:** El ingeniero consulta el grafo para obtener recomendaciones o ejemplos previos.
4. **Evolución:** Cada nuevo proyecto enriquece la base de conocimiento global.
