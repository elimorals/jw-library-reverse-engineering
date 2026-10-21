# Media, descarga y origen del contentKey

## API del mediador

- Endpoint confirmado en código: **`/apis/pub-media/GETPUBMEDIALINKS`** (host
  `app.jw-cdn.org` / `b.jw-cdn.org`). Es la API pública de JW que, dado símbolo de
  publicación + idioma + tipo, devuelve los enlaces de media (audio/video) y metadatos.
- Implementado por `org.jw.mediator.*` y `org.jw.pubmedia.*` (modelo ofuscado a..j;
  la deserialización vive en `org.jw.pubmedia.f`).
- `DownloadService` (`org.jw.pal.download`) + `PublicationDownloader` /
  `MediaDownloader` (vistos en `SiloContainer`) ejecutan las descargas.

## contentKey → clave de descifrado

- `contentKey` aparece en el pipeline de contenido (`org.jw.jwlibrary.mobile.page.*`:
  `n1`, `r`, `t1`). Es el material que, por la cadena del paquete `ob` (doc 02),
  alimenta **Argon2id → XChaCha20-Poly1305** para descifrar el contenido protegido.
- Construcción del descifrador: `LibraryApplication$w$j` instancia `ob.d` (parser de
  token) → la clave se **deriva en el dispositivo**, no se transmite ni se guarda.

## Interpretación (con matiz)

El `contentKey`/token llega **asociado a cada publicación/media** (vía catálogo o la
respuesta del mediador) y sirve de semilla para la derivación. No confirmé si el
token es por-publicación público (solo ofuscación/empaquetado) o por-cuenta (DRM
real ligado a usuario): el origen exacto del valor del token queda como hipótesis.
`licenses.html` sugiere términos de licencia de contenido.

## `org.jw.service.metrics`
Una sola clase: **`Rank`** (`float a`) — puntuación para ordenar resultados de
búsqueda. **No es telemetría.** Refuerza la conclusión del doc 08: la app no tiene
un sistema de métricas de uso activo.
