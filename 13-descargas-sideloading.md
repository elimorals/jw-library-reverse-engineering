# Descargas y sideloading

## Componentes

| Clase | Rol |
|---|---|
| `org.jw.pal.download.DownloadService` | Foreground service (`dataSync`) que ejecuta descargas |
| `org.jw.service.library.PublicationDownloader` | Descarga de publicaciones (`.jwpub`) |
| `MediaDownloader` | Descarga de media (audio/video) |
| `org.jw.jwlibrary.sideloading.platform.ImportService` | Importación local (JobService) |
| `org.jw.jwlibrary.mobile.sideloading.ImportDialogActivity` | Diálogo de importación |

`SiloContainer` suscribe a `PublicationDownloader.b()` y `MediaDownloader.d()` para
reflejar progreso en la UI (doc 03, `onCreate`).

## Flujo de descarga (online)

1. La API del mediador **`/apis/pub-media/GETPUBMEDIALINKS`** (host `app.jw-cdn.org`)
   devuelve los enlaces de media + metadatos (doc 09).
2. `DownloadService` descarga sobre HTTPS (capa de red: okhttp3, doc 15).
3. El contenido se **verifica por firma** (Ed25519, doc 02) y, si aplica, se
   **descifra** (XChaCha20-Poly1305 con clave Argon2id, doc 02).
4. Se registra en el catálogo/biblioteca (`catalog.db` / Realm) y MEPS lo indexa.

## Flujo de sideloading (local)

Entrada por `SiloContainer.F()` (doc 03):
- `SEND`/`SEND_MULTIPLE` o `content://`/`file://`:
  - archivo `.jwpub` → `B(uri,"jwpub")` → `ImportService` → verificación + import.
  - archivo `.jwlibrary` → `B(uri,"jwlibrary")` → import de **backup** de usuario.
  - otro → `ImportDialogActivity` (el usuario decide).
- Todo `.jwpub` importado pasa por la **misma verificación de firma** que el online:
  un archivo manipulado falla con `pb/j: InvalidFileSignature` (doc 02). El
  sideloading no evita la cadena de confianza.

## Seguridad
El sideloading es el vector de entrada de archivos no confiables, pero está acotado
por la verificación criptográfica obligatoria. Riesgo residual: bugs de parsing en el
SQLite/ZIP del `.jwpub` antes de validar la firma (no auditado a nivel de fuzzing).
