# LogPose · logcore/tools

Módulo: `logcore/tools`
Package: `com.uriel.logpose.logcore.tools`

Biblioteca base de utilidades reutilizable por LogCore, Thamis, LogProbe, Voice, Music,
Bluetooth y cualquier módulo futuro. No depende de UI, Activities, Fragments ni Compose.

## Estructura

```
tools/
  TimeTools.kt          DateTools.kt         DurationTools.kt
  StringTools.kt         RegexTools.kt        CollectionTools.kt
  MapTools.kt             ListTools.kt          NumberTools.kt
  MathTools.kt            RandomTools.kt        JsonTools.kt
  FileTools.kt            PathTools.kt          StreamTools.kt
  EncodingTools.kt        HashTools.kt          ChecksumTools.kt
  CompressionTools.kt     ZipTools.kt           ReflectionTools.kt
  ClassTools.kt           PackageTools.kt       ThreadTools.kt
  ConcurrencyTools.kt     FutureTools.kt        CoroutineTools.kt
  AndroidTools.kt         IntentTools.kt        BundleTools.kt
  UriTools.kt             PermissionTools.kt    ValidationTools.kt
  IdentifierTools.kt      IdGenerator.kt        UuidTools.kt
  ClockTools.kt           BenchmarkTools.kt     LoggerTools.kt
  ExceptionTools.kt       RetryTools.kt         ResourceTools.kt
```

42 archivos, cada uno con una única responsabilidad, KDoc en todas las funciones públicas
y sin estado global salvo donde aporta una utilidad real (contadores de `IdGenerator`,
pool compartido de `ThreadTools`).

## Dependencias

La mayoría de los archivos son Kotlin puro sobre la JVM (`java.*`, `kotlin.*`) y no requieren
nada adicional. Tres grupos de archivos sí requieren dependencias externas al SDK de Kotlin
estándar, ya presentes de forma habitual en un proyecto Android:

- **`JsonTools.kt`** usa `org.json` (incluido en el SDK de Android; no es una librería externa
  nueva). Si el módulo se compilara fuera de Android (JVM puro), se debe agregar la dependencia
  `org.json:json`.
- **`CoroutineTools.kt` y `RetryTools.kt`** usan `kotlinx.coroutines` (`kotlinx-coroutines-core`
  y, para `onMain`, `kotlinx-coroutines-android`). Es la librería estándar de facto para
  concurrencia en Kotlin/Android; se consideró "realmente necesaria" según la regla del proyecto.
- **`AndroidTools.kt`, `IntentTools.kt`, `BundleTools.kt`, `UriTools.kt`, `PermissionTools.kt`**
  usan el SDK de Android (`android.content.*`, `android.os.*`, `android.net.*`) y, en el caso de
  `PermissionTools`, `androidx.core:core-ktx` (`ContextCompat`). Son necesarios porque esas
  clases envuelven directamente APIs de Android (Intent, Bundle, Uri, permisos); no son
  Activities, Fragments ni Compose, por lo que respetan la restricción del encargo.

## Supuestos documentados

1. **`LoggerTools.kt`** asume que ya existe, en el módulo LogCore (no modificado por esta
   entrega), una clase `com.uriel.logpose.logcore.LogCoreLogger` con funciones `d`, `i`, `w`, `e`
   de firma `(tag: String, message: String, throwable: Throwable? = null)`. Si la firma real
   difiere, solo hace falta ajustar la implementación interna de `LoggerTools`; el resto del
   módulo no depende de ese detalle.
2. **Verificación de compilación**: este entorno de generación no tiene acceso al SDK de
   Android, a Gradle ni a red para descargar `kotlinc`/dependencias, por lo que el módulo no
   pudo compilarse de punta a punta contra el proyecto real. Se revisó manualmente cada archivo
   (imports, tipos, firmas, ausencia de APIs deprecated) y se mantiene una única fuente de la
   verdad por responsabilidad para minimizar el riesgo de error. Se recomienda compilar el
   módulo dentro del proyecto Android real como primer paso de integración.
3. **`JsonTools`** cubre el caso general de mapas/listas anidadas con tipos primitivos (String,
   número, boolean, null, Map, List). No se implementó serialización tipada de clases de datos
   (data classes) porque el encargo no especificó un modelo concreto y añadir un framework de
   serialización reflexiva iría contra la regla "sin reflexión salvo ReflectionTools".
4. **`ZipTools.unzip`** valida cada ruta de salida contra "zip slip" (path traversal) como
   medida de seguridad por defecto, no solicitada explícitamente pero considerada necesaria
   para cualquier función que descomprime archivos de origen no confiable.
5. **`ThreadTools`** mantiene un único `ScheduledExecutorService` compartido de nivel de objeto
   (pool de background). Se documenta como el único "singleton" de utilidad real del módulo,
   junto con el `Handler` de hilo principal en `AndroidTools`.

## Lista de funciones públicas por archivo

- **TimeTools**: `now`, `nowNanos`, `elapsed`, `elapsedNanos`, `measure`, `measureMillis`, `sleep`
- **DateTools**: `format`, `formatMillis`, `parse`, `parseIso`, `timezone`, `systemTimezone`, `isValid`
- **DurationTools**: `ofMillis`, `ofSeconds`, `humanReadable`, `add`, `subtract`, `isLongerThan`, `clamp`
- **StringTools**: `normalizar`, `capitalizar`, `camelCase`, `snakeCase`, `trim`, `removeAccents`, `safeEquals`, `containsIgnoreCase`
- **RegexTools**: `matches`, `containsMatch`, `findAll`, `firstGroup`, `replaceAll`, `split`, `escape`
- **CollectionTools**: `safeGet`, `filterNotNull`, `group`, `index`, `isNullOrEmpty`, `chunk`, `intersect`
- **MapTools**: `getOrDefault`, `invert`, `merge`, `filter`, `mapValues`, `isNullOrEmpty`, `synchronizedMap`
- **ListTools**: `distinctPreservingOrder`, `lastOrNull`, `firstOrNull`, `move`, `insert`, `partition`, `isSorted`, `immutableCopy`
- **NumberTools**: `clamp` (Int/Double/Long), `round`, `roundToInt`, `average`, `percentage`, `isInRange`, `parseDoubleOrNull`, `parseIntOrNull`
- **MathTools**: `min`, `max`, `interpolate`, `normalize`, `mapRange`, `gcd`, `lcm`
- **RandomTools**: `int`, `double`, `boolean`, `pick`, `shuffle`, `alphanumeric`
- **JsonTools**: `serialize`, `serializeList`, `deserialize`, `deserializeList`, `prettyPrint`, `isValidJson`
- **FileTools**: `leer`, `escribir`, `agregar`, `copiar`, `mover`, `borrar`, `crearDirectorios`, `existe`, `tamano`
- **PathTools**: `join`, `extension`, `fileName`, `fileNameWithoutExtension`, `parent`, `normalize`, `isAbsolute`, `resolve`
- **StreamTools**: `readAllBytes`, `copy`, `closeQuietly`, `readAsText`
- **EncodingTools**: `base64Encode`, `base64Decode`, `base64DecodeToString`, `urlEncode`, `urlDecode`, `toHex`, `fromHex`
- **HashTools**: `md5`, `sha1`, `sha256`, `matchesSha256`
- **ChecksumTools**: `crc32` (bytes/file), `adler32`, `matchesCrc32`
- **CompressionTools**: `compress`, `decompress`, `compressionRatio`
- **ZipTools**: `zip`, `unzip`, `listar`
- **ReflectionTools**: `simpleClassName`, `qualifiedClassName`, `getPropertyValue`, `propertyNames`, `isInstanceOf`
- **ClassTools**: `simpleName`, `qualifiedName`, `isSubclassOf`, `isInterface`, `isEnum`
- **PackageTools**: `lastSegment`, `parent`, `isSubPackageOf`, `join`, `isValid`
- **ThreadTools**: `background`, `main`, `delay`, `isCurrentThread`, `currentThreadName`
- **ConcurrencyTools**: `withLock`, `atomicInt`, `atomicLong`, `atomicBoolean`, `runOnce`
- **FutureTools**: `completed`, `async`, `awaitOrNull`, `combine`, `allOf`
- **CoroutineTools**: `onIO`, `onDefault`, `onMain`, `delaySafe`, `withTimeoutOrNullSafe`, `asyncIn`
- **AndroidTools**: `isMainThread`, `runOnMainThread`, `runOnMainThreadDelayed`, `appVersionName`, `appVersionCode`, `isAtLeast`
- **IntentTools**: `shareText`, `openUrl`, `openAppSettings`, `canResolve`, `withChooser`
- **BundleTools**: `getString`, `getInt`, `getBoolean`, `getLong`, `of`, `merge`
- **UriTools**: `fromFile`, `isContentUri`, `isFileUri`, `displayName`, `readBytes`, `queryParam`
- **PermissionTools**: `isGranted`, `areAllGranted`, `missingPermissions`
- **ValidationTools**: `email`, `url`, `phone`, `uuid`, `json`, `notBlank`, `lengthInRange`
- **IdentifierTools**: `isValidKotlinIdentifier`, `isValidSlug`, `toSlug`, `toKey`
- **IdGenerator**: `nextSequential`, `shortId`, `withPrefix`, `resetSequence`
- **UuidTools**: `generate`, `generateUuid`, `isValid`, `deterministic`, `parseOrNull`
- **ClockTools**: `system`, `fixed`, `mutable` (+ interfaz `Clock`, clase `MutableClock`)
- **BenchmarkTools**: `measureExecution`, `measureAverage`, `measureAll`
- **LoggerTools**: `d`, `i`, `w`, `e`
- **ExceptionTools**: `messageOrDefault`, `rootCause`, `stackTraceAsString`, `runCatchingOrDefault`, `isOneOf`
- **RetryTools**: `retry`, `retryWithDelay`, `retryUntil`
- **ResourceTools**: `useAll`, `withResource`

## Calidad

- 100% Kotlin, sin Java.
- Sin APIs deprecated (uso de `longVersionCode` en vez de `versionCode` en API 28+, etc.).
- Sin `TODO`, sin `FIXME`, sin código muerto.
- Reflexión limitada exclusivamente a `ReflectionTools.kt`.
- Una responsabilidad por clase/objeto; funciones puras donde el dominio lo permite
  (excepciones necesarias: I/O de archivos, red de nombres, reloj del sistema, generación
  de aleatorios/IDs, wrappers de Android).
