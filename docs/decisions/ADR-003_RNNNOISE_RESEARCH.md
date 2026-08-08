# ADR-003: RNNNoise for Speech Enhancement

## Status
PROPOSED (Research Council)

## Context
At speeds over 80km/h, wind noise becomes the primary cause of Intent Accuracy (IAS) degradation. Current digital filters are linear and struggle with non-stationary wind buffeting.

## Decision
Investigate the integration of **RNNNoise** (a hybrid DSP/Deep Learning noise suppressor) as a pre-processor for the Vosk ASR engine.

## Consequences
- **Pros**: Significant SNR improvement, low CPU overhead (designed for real-time), no cloud dependency.
- **Cons**: Requires JNI integration in Android, potential latency increase of 10-15ms.

## Success Metric
- FTSR improvement of > 20% in "WIND_HIGH" scenarios.
