# LogPose Fixes & Brain Updates

## 1. VoiceManager: Anchor Verbs Expansion
**Problem:** Commands like "cancelar viaje" or "detener navegación" were being filtered out before reaching THAMIS because they didn't start with a recognized "anchor verb".
**Fix:** Added 10 new anchor verbs to `VoiceManager.kt`:
- `cancelar`, `terminar`, `terminá`, `detener`, `parar`, `cortar`, `sacá`, `sacar`, `dejá`, `dejar`.
**Result:** THAMIS now correctly receives these phrases and maps them to `Intent.STOP_NAVIGATION`.

## 2. MusicManager: Spotify Disambiguation
**Problem:** Spotify search intents were sometimes ambiguous, leading to "Search results" pages instead of auto-playing.
**Fix:** Added `MediaStore.EXTRA_MEDIA_ARTIST` hint to the intent.
**Result:** Higher probability of auto-play for artists and short queries.

## 3. PC Orchestration
- **Stop Navigation:** Now sends a UDP command to PC to "cerrar mapas".
- **Play Music:** Now notifies PC to "poner [app]" for cross-device awareness.
- **Switch Tab:** Added `Intent.SWITCH_TAB` support for browser control.
- **Future Vision:** Sembrado de intenciones para `SMART_TV_CONTROL` e `IOT_CONTROL`.
