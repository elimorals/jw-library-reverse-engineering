# Deep links, intents e importación de archivos

Toda la entrada externa converge en **`org.jw.jwlibrary.mobile.activity.SiloContainer`**
(`exported=true`, `launchMode=singleTask`).

## Esquemas e intent-filters de SiloContainer

| Filtro | Qué acepta |
|---|---|
| `jwlibrary://…` (VIEW, BROWSABLE) | Deep link interno a contenido (navegación a publicación/versículo) |
| `jwlplaylist://…` (VIEW, BROWSABLE) | Compartir/abrir listas de reproducción |
| `content://` + `file://` con `mimeType=*/*` (VIEW) | Abrir cualquier archivo (sideloading de `.jwpub`, `.jwlplaylist`, media) |
| SEND / SEND_MULTIPLE (`video/mp4`, `video/m4v`, `audio/mp3`, `audio/mpeg`) | Recibir media compartida desde otras apps |
| App Link `https://www.jw.org/finder` (`autoVerify=true`) | Enlaces web verificados → abren en la app |
| `android.intent.action.SEARCH` | Búsqueda del sistema (`searchable.xml`) |

Esquema adicional declarado en `<queries>`: `jwlibrarysl://` (sideloading) y `https`.

## Flujo de importación (sideloading)

- `org.jw.jwlibrary.mobile.sideloading.ImportDialogActivity` — diálogo de importación.
- `org.jw.jwlibrary.sideloading.platform.ImportService` — servicio (BIND_JOB_SERVICE, dataSync).
- Un `.jwpub` o media llega por `content://`/`file://` → importación → **se valida
  criptográficamente** por el subsistema `pb`/DSS (doc 02) antes de confiar en él.

## Enrutamiento real (decompilado) — `SiloContainer` → `M8.j`

`SiloContainer.onNewIntent` delega todo intent que no sea SEARCH al router
`M8.j.p(intent, activity)` (resuelto por el service locator `J8.c`). El router
(código en `decompiled/M8.j.*.java`) decide en 3 pasos:

```
p(intent):
  url = o(intent)                      // ¿deep link especial con token?
  if url != null:  pasar por NetworkGate/LockedGate  (corrutina)
  else if !F(intent): D(intent)
  finally: intent.setData(null)        // evita reprocesar
```

### `o()` — canal beta con token
```java
if (scheme=="jwlibrary" && host=="jwl-beta-dropbox")
    return data.getQueryParameter("token");
```
Único deep link que `o` reconoce: **`jwlibrary://jwl-beta-dropbox?token=<X>`**.
Entra por cualquier app/navegador (BROWSABLE) y el token se procesa tras un
`NetworkGate`. Es un mecanismo de distribución **beta**. Vector a tener en cuenta:
un token atacante llega al gate; el impacto depende de qué hace el gate (pendiente).

### `F()` — compartir e importar archivos
- `SEND`/`SEND_MULTIPLE`: si el archivo termina en **`.jwpub`** → importa publicación
  (`B(uri,"jwpub")`); si **`.jwlibrary`** → importa backup (`B(uri,"jwlibrary")`);
  si no, abre `ImportDialogActivity`.
- `VIEW` con `content://` ajeno: consulta `_display_name` por ContentResolver y deriva
  la importación. Los esquemas `http(s)` y `jwlibrary` se devuelven a `D`/gate.

### `D()` — VIEW genérico
Despacha a `Dispatcher` → `j.E` → **`r9.h.d(context, uri)`** (resolvedor real,
`decompiled/r9.h.d_uriResolver.java`):

```
b = new k().g(uri)                 // parser de URI → objeto mb.b (host/lang/path)
if (b == null) → no navegar
else if scheme == "jwlibrary"      → finderNavigator.a(ctx, uri, b, …)  (interno)
else (https finder):
    lang = idioma de b.f()
    if (lang es de señas / descargado):
        reescribe uri a scheme "jwlibrarysl"
        si hay app que lo maneje → startActivity  (abre JW Library Sign Language)
        si no → navegación interna
    else → navegación interna
```

Hallazgo: los App Links `www.jw.org/finder` cuyo contenido es en **lengua de señas**
se **redirigen a la app hermana `jwlibrarysl`** (JW Library – Sign Language) si está
instalada. El parseo de la URL (`finder`) lo hace la clase `k` → `mb.b`.

> Todo `.jwpub`/`.jwlibrary` importado pasa luego por la **verificación de firma
> `pb`/DSS** (doc 02) antes de confiarse.

## Superficie de ataque (deep links)

`SiloContainer` es el componente más expuesto. Vectores a revisar en una auditoría:

1. **Parsing de URIs `jwlibrary://` / `jwlplaylist://`**: inyección de parámetros,
   path traversal al resolver a recursos locales, navegación forzada.
2. **`content://` con `mimeType=*/*`**: la app abre archivos arbitrarios de otras
   apps. El riesgo lo acota la **verificación de firma** del contenido importado;
   un `.jwpub` manipulado debería fallar en `pb/j: InvalidFileSignature`.
3. **App Link `/finder`**: `autoVerify` exige `assetlinks.json` en `www.jw.org`.
4. **`file://` + `root-path "/"` del FileProvider** (doc 05): combinación a vigilar.

## Pendiente (opcional)
- [ ] Decompilar el handler de `onCreate`/`onNewIntent` de `SiloContainer` para ver
      el parser de URI exacto y cómo mapea host/path a acciones.
