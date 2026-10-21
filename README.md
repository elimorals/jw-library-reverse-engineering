# Ingeniería inversa de JW Library 15.9.2 — índice

Análisis **estático** del APK `jwlb_E.apk` con REA 6.7.0 (JADX 0.7.1 + apktool).
La aplicación nunca se ejecutó. Es la app oficial pública de Watch Tower.

## Documentos

| # | Archivo | Tema |
|---|---|---|
| 00 | [00-RESUMEN.md](00-RESUMEN.md) | Identidad, pila tecnológica, mapa de módulos |
| 01 | [01-manifiesto.md](01-manifiesto.md) | Permisos, componentes exportados, superficie de ataque |
| 02 | [02-cripto-firmas.md](02-cripto-firmas.md) | libsodium/Argon2/Tink y **PKI de contenido con revocación** |
| 03 | [03-deeplinks.md](03-deeplinks.md) | Esquemas URI, router `M8.j` (decompilado), importación |
| 04 | [04-webapp-js.md](04-webapp-js.md) | Motor **Javet/V8** + webapp **React** + puente nativo |
| 05 | [05-red-endpoints.md](05-red-endpoints.md) | TLS, network security config, endpoints prod/dev/QA |
| 06 | [06-datos-almacenamiento.md](06-datos-almacenamiento.md) | Realm, SQLite, catálogo (4,625 pubs), backup |
| 07 | [07-bridge-nativeinterface.md](07-bridge-nativeinterface.md) | Protocolo JS↔Java: 54 mensajes `NativeBridgeSender` + eventos custom |
| 08 | [08-telemetria.md](08-telemetria.md) | App Center (inactivo) y analytics interno |
| 09 | [09-media-contenido.md](09-media-contenido.md) | API GETPUBMEDIALINKS, descargas, contentKey |
| 10 | [10-motor-meps.md](10-motor-meps.md) | Motor MEPS, formato `.jwpub`, modelos de contenido |
| 11 | [11-webapp-paneles.md](11-webapp-paneles.md) | Paneles React ↔ controladores Java, dispatcher del bridge |
| 12 | [12-nativo-so.md](12-nativo-so.md) | Librerías `.so` desensambladas (libsodium-wrapper verificada) |
| 13 | [13-descargas-sideloading.md](13-descargas-sideloading.md) | DownloadService, PublicationDownloader, import `.jwpub` |
| 14 | [14-media-reproduccion.md](14-media-reproduccion.md) | ExoPlayer, AudioService, PiP, Android Auto, notas/marcas |
| 15 | [15-ui-navegacion-conexiones.md](15-ui-navegacion-conexiones.md) | Pantallas MVVM, Compose+Vistas, navegación, red, Wear, a11y |
| 16 | [16-firma-apk-recursos.md](16-firma-apk-recursos.md) | Firma v2/v3 verificada, búsqueda, 4,144 recursos |
| 17 | [17-analisis-dinamico.md](17-analisis-dinamico.md) | Ejecución en emulador: arranque, onboarding, bug, datos en reposo (root) |
| 18 | [18-frida-instrumentacion.md](18-frida-instrumentacion.md) | Frida: verificación de firma Ed25519 capturada EN VIVO |
| 19 | [19-pruebas-finales.md](19-pruebas-finales.md) | mitmproxy/CA de sistema y gate de verificación demostrado |
| 20 | [20-proyectos-similares.md](20-proyectos-similares.md) | Estado del arte y aporte diferencial |

## Artefactos

| Carpeta | Contenido |
|---|---|
| `decompiled/` | Java: `SiloContainer.*`, router `M8.j.*`, resolvedor `r9.h.d`, verificador `pb.c.b`, keyManager `z9.a.b`, KDF `ob.b.a`, token `ob.d.a` |
| `assets-extract/` | Bundles JS de la webapp (React), `catalog.db`, scripts `.sql` |
| `res-decoded/` | APK decodificado por apktool (manifiesto, res/xml, smali, assets, lib) |

## 5 hallazgos principales

1. **Arquitectura híbrida Javet/V8 + React**: las publicaciones se renderizan con un
   motor V8 embebido (no WebView) ejecutando bundles React; puente vía
   `window.NativeInterface.send` + `postMessage`.
2. **PKI de contenido de 2 niveles con revocación** (`org.watchtower.dss` + paquete
   `pb`): verificación Ed25519 (libsodium) de root keys → signing keys → contenido,
   con listas de revocación firmadas. **Root key embebida** (Ed25519, hex
   `f52049…ac18`, en `z9.a`). Contenido además **cifrado con XChaCha20-Poly1305**,
   clave derivada por **Argon2id** (token Base32-Crockford + `saltPacket`). App
   Center está empaquetado pero **inactivo**. Defensa E2E independiente de TLS.
3. **Superficie exportada acotada** salvo `SiloContainer` (deep links + importación de
   `.jwpub`/media) y un residuo: `androidx...PreviewActivity` exportada en release.
4. **Higiene de red/FS**: `dev-library.jw.org` con cleartext HTTP y dominios dev/QA
   (`whqmeps.org`, `*-qa`) en el release; FileProvider con `root-path "/"`.
5. **Sin permisos peligrosos** (ni ubicación, cámara, micrófono, contactos). Perfil de
   privacidad conservador; backup solo de `userData.db`.

## Alcance, propósito y límites legales

Este repositorio contiene **análisis original** (observaciones, diagramas y notas de
arquitectura/seguridad) de la app pública **JW Library**, con fines de aprendizaje e
investigación de seguridad sobre un artefacto que se posee. **No redistribuye la obra
ajena**: no incluye el APK, los recursos decodificados, los bundles de la webapp, las
bases de datos, el contenido de publicaciones, ni capturas con texto de la app — todo
eso es propiedad de Watch Tower y queda fuera (ver `.gitignore`). Los fragmentos de
código decompilado incluidos son mínimos y sirven solo para sustentar un hallazgo.

No se reconstruye ni se clona la aplicación. La instrumentación con Frida se usó para
**observar y confirmar** controles de seguridad (verificación de firma), no para evadir
protecciones ni para acceder a contenido sin autorización.

## Proyectos similares
Ver [20-proyectos-similares.md](20-proyectos-similares.md): no hay trabajo público de RE
del APK; los proyectos existentes parsean el formato `.jwpub`, no analizan el binario.

## Limitaciones

Observaciones estáticas; no establecen comportamiento en runtime. Firma del APK no
verificada. Nombres `pb/*`, `M8.*`, `J8.*` están ofuscados por R8; las
interpretaciones se basan en símbolos y strings conservados.
