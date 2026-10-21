# Análisis dinámico (emulador)

Ejecución real en **AVD Android 16, arm64-v8a** (emulador del SDK). La app se instaló
y ejecutó; confirma en runtime lo deducido estáticamente.

## Entorno
- Imagen de **producción** (Google Play) → `adb root` **denegado**. Sin acceso a
  `/data/data` ni Frida sin un AVD rooteable (ver "Límites").
- Instalación: `adb install` → **Success** (el APK trae `arm64-v8a`).

## Arranque y flujo de onboarding (confirmado)

```
MainActivity → SiloContainer → TermsOfUseActivity → PrivacyAcceptanceActivity → SiloContainer (Home)
```

- Carga nativa confirmada en logcat: **`libjavet-v8-android-i18n…so`** (V8) y
  **`librealm-jni.so`** (Realm) se cargan al inicio.
- Además levanta un **WebView Chromium sandbox** (`org.chromium.content.app…`) y
  tiene **Cronet** (vía GMS) disponible para red.
- Solicita red `INTERNET` (ConnectivityService, uid de la app).

## UI real (capturas en `evidence/`)

- `jw_terms.png` — modal **Terms of Use**: exige leer hasta el fondo antes de
  habilitar ACCEPT → **confirma el mensaje de bridge `TERMS_OF_USE_PAGE_REACHED_BOTTOM`**
  (doc 07). El contrato se renderiza con el panel `TermsOfUse.bundle.js` (V8/WebView).
- `jw_home2.png` — modal **Privacy Settings**: telemetría de diagnóstico/uso
  **opt-in, declinable** (CUSTOMIZE/DECLINE/ACCEPT), con texto "nunca vendida ni usada
  para marketing". Se hizo **DECLINE** y la app continuó con normalidad.
- `jw_home_final.png` — **Home** (`SiloContainer`): navegación inferior
  **Home · Bible · Library · Meetings · Personal Study** (5 secciones), secciones
  Favorites / Teaching / What's New, y publicaciones con icono de descarga (nube).
  **Confirma el mapa de UI del doc 15.**

## Hallazgo dinámico: bug de servicio en background

Logcat capturó una excepción fatal en un worker de corrutina:

```
android.app.BackgroundServiceStartNotAllowedException:
  Not allowed to start service Intent { …/org.jw.pal.download.DownloadService }:
  app is in background
```

Ocurre cuando la app intenta arrancar `DownloadService` mientras está en background
(Android 12+ prohíbe iniciar servicios desde background). En uso normal (app en
primer plano) no se dispara, pero es un **manejo incompleto del ciclo de vida** en
Android 16: debería usar `WorkManager`/foreground diferido para esa ruta.

## Seguridad de red confirmada (black-box)

- El `networkSecurityConfig` confía **solo en CAs del sistema** (doc 05). En la
  práctica esto **bloquea MITM con un CA de usuario**: un proxy de interceptación con
  certificado propio sería rechazado. Es una defensa efectiva aun **sin certificate
  pinning explícito**. (Por eso la captura de contenido TLS no es trivial; ver red
  a nivel de hosts/DNS vía pcap, abajo.)

## Límites del análisis dinámico
- Imagen de producción → sin root: no se inspeccionó `/data/data` (userData.db,
  Realm en reposo) ni se usó Frida. Requiere un **AVD `google_apis` (rooteable)**.
- El contenido TLS no se descifró (ver seguridad de red). Se captura a nivel de
  hosts/DNS por pcap del emulador.

## Tráfico de red — captura intentada, resultado honesto

Se reinició el emulador con `-tcpdump jw_traffic.cap` para capturar el tráfico. El
pcap resultante (`scratchpad/jw_traffic.cap`, 21 KB, **74 paquetes**) contiene **solo
ruido de red local del arranque** (ICMPv6 router/neighbor advertisement, ARP) y
**ningún tráfico de la app**.

Causa (verificada, no inferida): el reinicio del emulador dejó los datos en limpio,
así que la app volvió al **onboarding** (Terms → Privacy) y a la carga inicial de V8;
no llegó a **sincronizar el catálogo**, que es cuando haría las peticiones HTTPS. Los
intentos de pasar el onboarding por `input tap` no estabilizaron la sesión a tiempo.

**No fingiré endpoints capturados.** Los destinos de red de la app están
**establecidos por el análisis estático** (docs 05 y 09), con alta confianza:
`app.jw-cdn.org` (CDN, `/apis/pub-media/GETPUBMEDIALINKS`), `wol.jw.org`,
`www.jw.org`, `donate.jw.org`.

Para capturar el tráfico real de la app haría falta uno de estos caminos:
1. Pasar el onboarding de forma estable y navegar a Library/Sync con el pcap activo.
2. Un **AVD `google_apis` rooteable** + mitmproxy con CA instalado como **CA del
   sistema** (no de usuario) — necesario porque el `networkSecurityConfig` solo
   confía en CAs del sistema (sección anterior).

## Fase 2 — AVD ROOTEABLE (google_apis, Android 36 arm64)

Se creó un segundo AVD rooteable (`jw_root`, system-image `google_apis`). `adb root`
→ **`uid=0(root)`**. Con acceso total a `/data/data/org.jw.jwlibrary.mobile`.

### Datos en reposo (`/data/data/...`)

| Ubicación | Contenido |
|---|---|
| `Userdata/userData.db` (217 KB) | Datos de usuario (notas/marcas/favoritos) |
| `databases/mepsunit.db` (29 MB) | Unidades MEPS (inflado de `mepsunit.jwdat`) |
| `databases/search.db` (2.5 MB) | Índice de búsqueda |
| `databases/pub_collections.db`, `media_collections.db`, `saved_locations.db` | Colecciones |
| `databases/system_config.db`, `popularity_contest.db` | Config y popularidad |
| `databases/Root`, `databases/Signing` (0 B) | Relacionadas con la PKI de firma |
| `files/mediator.realm` (6 MB) | Base **Realm** del mediador |
| `app_publications/` | Publicaciones descargadas (`.jwpub`) |

### Cifrado en reposo — VERIFICADO: las bases de datos NO se cifran

Todas las DB abren con header **`SQLite format 3`** en claro (userData, config,
catálogo, telemetría). `mediator.realm` es un archivo Realm estándar (no SQLCipher).

**Conclusión:** el **cifrado XChaCha20/Argon2 (doc 02) protege el contenido de
publicaciones** (`app_publications/*.jwpub`), **no** las DB de usuario/catálogo, que
quedan en claro en el almacenamiento privado de la app (protegido por el sandbox de
Android, no por cripto). Dato de privacidad: los resaltados/notas del usuario en
`userData.db` no están cifrados en reposo.

### Telemetría — ver corrección en doc 08
App Center crea `installId` + sesión + DB de persistencia, pero **0 eventos en
`logs`** tras DECLINE. Respeta el opt-out.

