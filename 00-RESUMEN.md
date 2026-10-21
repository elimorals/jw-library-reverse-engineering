# JW Library — Ingeniería inversa (REA)

> Análisis **estático** del APK `jwlb_E.apk`. La app nunca se ejecuta.
> Herramienta: REA 6.7.0 + JADX headless 0.7.1 + apktool. Fecha: 2026-10-20.

## Identidad del artefacto

| Campo | Valor |
|---|---|
| Archivo | `~/Downloads/jwlb_E.apk` (149,868,753 bytes) |
| SHA-256 | `7708a107b0f444ee2bb9e10fd21067a615fd5a3792e7bdbd5a34529d21417515` |
| Paquete | `org.jw.jwlibrary.mobile` |
| App | JW Library (oficial, Watch Tower Bible and Tract Society) |
| Versión | 15.9.2 (versionCode 973199) |
| minSdk / targetSdk | 26 / 36 (compileSdk 36 / Android 16) |
| Clases | 9,107 · Recursos | 4,144 |
| DEX | classes.dex (7.28 MB) + classes2.dex (7.55 MB) |
| Firma APK | no verificada por el motor |

## Pila tecnológica

| Capa | Tecnología | Evidencia |
|---|---|---|
| UI | Jetpack Compose + AppCompat + Views clásicas | `androidx.compose.*` (261 clases), `PreviewActivity` |
| Motor de contenido | **Javet + V8** (JS embebido) | `libjavet-v8-android-i18n.v.4.1.5.so` (~75 MB/ABI), `com.caoccao.javet.*` (440 clases), `assets/webapp` (224 archivos) |
| Base de datos | **Realm** (objetos) + **SQLite** (requery) | `librealm-jni.so`, `libsqlite3x.so`, `io.realm.*`, `io.requery.android.*`, `assets/catalog.db`, `vN_upgrade.sql` ×16 |
| Cripto | **libsodium** (Ed25519) + **Argon2** + **Google Tink** | `org.watchtower.dss.SodiumHelper`, `com.lambdapioneer.argon2kt.*`, `com.google.crypto.*` (69), `libsodium*.so`, `libargon2*.so` |
| Serialización | kotlinx.serialization + Gson | `kotlinx.serialization.*` (162), `com.google.gson.*` (61) |
| Imágenes | Glide + metadata-extractor (EXIF) | `com.bumptech.glide.*` (56), `com.drew.metadata.*` (98) |
| Telemetría | Microsoft App Center | `com.microsoft.appcenter.*` |
| Wearables | Wear OS | `android.support.wearable.*`, `uses-library com.google.android.wearable` |

## Módulos propios (org.jw / org.watchtower)

| Paquete | Clases | Rol |
|---|---|---|
| `org.jw.jwlibrary.mobile` | 449 | App principal (actividades, servicios, UI) |
| `org.jw.meps.common` | 58 | Motor **MEPS** (Multilanguage Electronic Publishing System) |
| `org.jw.service.*` | ~22 | Servicios: bible, library, media, metrics, translation, web |
| `org.jw.mediator.data` | 21 | Capa de datos/mediador |
| `org.jw.jwlibrary.ui` | 21 | UI compartida |
| `org.jw.pal.*` | ~6 | Platform Abstraction Layer (download, chrono, util) |
| `org.jw.pubmedia.a..j` | 10 | Ofuscado (R8) — modelo de media de publicaciones |
| `org.watchtower.dss.SodiumHelper` | 1 | Verificación de firmas Ed25519 del contenido |
| `org.watchtower.meps.jwlibrary` | 2 | Puente MEPS |

## Índice de documentos

- `01-manifiesto.md` — manifiesto, componentes exportados, permisos, superficie de ataque
- `02-cripto-firmas.md` — libsodium / Argon2 / Tink, verificación de contenido
- `03-deeplinks.md` — esquemas URI, SiloContainer, importación de archivos
- `04-webapp-js.md` — motor Javet/V8 y la webapp
- `05-red-endpoints.md` — network security config y endpoints
- `06-datos-almacenamiento.md` — Realm, SQLite, catálogo, backup
