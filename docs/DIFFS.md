# Sidebar Diffs for LogPose Fixes

## VoiceManager.kt
```kotlin
// Antes
val anchorVerbs = setOf(
    "abrí", "poné", "reproducí", "mensaje", "mandale", "mándale", "mandá", "manda", 
    "envía", "enviá", "ir", "leer", "leé", "ver", "cambiar", "ponele", "ponele", "mostrame", "mostráme"
)

// Después
val anchorVerbs = setOf(
    "abrí", "poné", "reproducí", "mensaje", "mandale", "mándale", "mandá", "manda", 
    "envía", "enviá", "ir", "leer", "leé", "ver", "cambiar", "ponele", "ponele", "mostrame", "mostráme",
    "cancelar", "terminar", "terminá", "detener", "parar", "cortar", "sacá", "sacar", "dejá", "dejar"
)
```

## MusicManager.kt
```kotlin
// Antes
val intent = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
    setPackage(SPOTIFY_PACKAGE)
    putExtra(SearchManager.QUERY, cleanQuery)
    putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/*")
    addFlags(...)
}

// Después
val intent = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
    setPackage(SPOTIFY_PACKAGE)
    putExtra(SearchManager.QUERY, cleanQuery)
    putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/*")
    putExtra(MediaStore.EXTRA_MEDIA_ARTIST, cleanQuery) // <--- Fix
    addFlags(...)
}
```
