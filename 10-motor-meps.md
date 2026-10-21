# Motor MEPS y formato de contenido (.jwpub)

**MEPS** = Multilanguage Electronic Publishing System, el motor de publicación de
Watchtower. En el APK vive en `org.jw.meps.common.*` (58 clases) con subpaquetes:

| Subpaquete | Clases | Rol |
|---|---|---|
| `jwpub` | 111* | Parser/modelo del formato de publicación `.jwpub` |
| `catalog` | 8 | Catálogo de publicaciones (↔ `catalog.db`) |
| `unit` | 7 | Unidades lingüísticas MEPS (↔ `mepsunit.jwdat`) |
| `library` | 7 | Biblioteca instalada |
| `datasource` | 6 | Acceso a datos |
| `libraryitem` | 3 | Ítems de biblioteca |
| `jwmedia` | 2 | Media |

(*incluye clases anidadas; el conteo de `org.jw.meps.common` top-level es 58.)

## Modelos de contenido (`jwpub`)

Clases legibles (no ofuscadas) que definen el dominio:

- `Publication`, `PublicationKey`, `PublicationCollection`, `PublicationViewItem`
- `Document` (unidad de lectura), `FootnoteContents`, `Topic`
- `Bible`, `BibleBook` (contenido bíblico)

## Formato `.jwpub` (según evidencia del binario)

Un `.jwpub` es un **contenedor** (ZIP) que incluye una base **SQLite** con los
documentos de la publicación más metadatos. En la importación (doc 03 → `F()` →
`B(uri,"jwpub")`), el contenido:

1. Se **verifica por firma** Ed25519 contra la root key confiada (doc 02, subsistema
   `pb`/`z9.a`), comprobando revocación.
2. Parte va **cifrado**: se descifra con XChaCha20-Poly1305 usando la clave derivada
   por Argon2id desde el `contentKey`/token (doc 02, paquete `ob`).
3. El motor MEPS parsea los `Document` y los entrega al render (webapp React vía el
   bridge `REQUEST_PRIMARY_CONTENT`/`REQUEST_STUDY_CONTENT`, doc 07).

## Unidades lingüísticas

`mepsunit.jwdat` (9.9 MB, asset) + `org.jw.meps.common.unit` resuelven idiomas MEPS,
mapeo de idiomas (`MepsLanguageId` del catálogo), reglas de escritura (de ahí las
fuentes Noto multi-script de la webapp, doc 04).

## Límite honesto
El parseo fino de cada tabla interna del SQLite de un `.jwpub` (nombres de tablas,
esquema de `Document`) no se extrajo clase por clase; se describe a nivel de modelo
y flujo. Las clases `jwpub/{A..I, a.1..}` son helpers ofuscados del parser.
