# ADR-006: AI Analysis Engine, Root Cause & Learning Core Architecture

## Context
THAMIS Lab requires an intelligent core (`:lab:intelligence`) to analyze test runs, determine root causes of failures, cluster patterns, detect regressions against baselines, calculate objective quality scores, generate non-intrusive fix recommendations, and maintain a relational Knowledge Graph.

## Decision
1. **AI Analysis Engine (`AiAnalysisEngine`)**: Facade evaluating log, error, scenario, performance, stability, and behaviour execution data.
2. **Root Cause Engine (`RootCauseEngine`)**: Correlates events and timeline traces to identify root events, failure chains, and impact levels.
3. **Pattern Recognition & Clustering (`PatternRecognitionEngine`)**: Groups recurring failures into error clusters.
4. **Regression Detector (`RegressionDetector`)**: Compares version accuracy and latency against historical baselines.
5. **Quality Engine (`QualityEngine`)**: Computes objective Overall, Stability, Reliability, and Robustness Scores.
6. **Recommendation Engine (`RecommendationEngine`)**: Generates actionable fix suggestions and risk analysis without auto-modifying code.
7. **Knowledge Graph (`KnowledgeGraph`)**: Thread-safe relational graph linking Errors, Events, Scenarios, and Devices.
8. **Reports & Metrics (`IntelligenceReportGenerator`, `GlobalMetricsEngine`)**: Generates Markdown, JSON, and executive reports.

## Consequences
- Transforms THAMIS Lab into an autonomous diagnostic ecosystem.
- 100% Pure Kotlin implementation with zero framework coupling and zero reflection.
