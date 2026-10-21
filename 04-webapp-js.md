# Motor de contenido: Javet/V8 + webapp React

JW Library **no** renderiza las publicaciones con un WebView clásico de Android:
embebe el motor **V8** vía **Javet** y ejecuta una **webapp React** que compone el
contenido. Es su diferenciador arquitectónico.

## Componentes

| Pieza | Evidencia |
|---|---|
| Motor JS | `libjavet-v8-android-i18n.v.4.1.5.so` (~75 MB/ABI, el archivo más pesado del APK) |
| Binding Java | `com.caoccao.javet.*` (440 clases) |
| ICU i18n | `assets/javet/icu/icudtl.dat` (8.9 MB) |
| UI | **React** (6 de 7 bundles contienen `_react`) |

## Bundles de la webapp (`assets/webapp/*.bundle.js`)

| Bundle | Tamaño | Función |
|---|---|---|
| `PrimaryPane` | 3.03 MB | Panel principal de lectura |
| `StudyPane` | 3.02 MB | Panel de estudio (notas, referencias cruzadas) |
| `NoteEditor` | 2.63 MB | Editor de notas |
| `BreakingNews` | 2.48 MB | Avisos / noticias |
| `TermsOfUse` | 2.48 MB | Términos de uso |
| `ImageDisplay` | 0.53 MB | Visor de imágenes |
| `MepsBlaster` | 0.31 MB | Render del contenido MEPS (motor de publicación) |

Cada bundle trae su `.LICENSE.txt` (dependencias OSS). HTML de entrada:
`breakingNews.html`. Fuentes multi-script en `assets/webapp/fonts/` (Noto: árabe,
bengalí, devanagari, georgiano, armenio… + DejaVu) — soporte multilingüe MEPS.

## Puente JS ↔ Java

La webapp se comunica con el lado nativo mediante:

- **`window.NativeInterface`** (18 refs) — objeto inyectado desde Java en el
  contexto V8 (vía Javet) que expone funciones nativas al JS.
- **`postMessage`** (18 refs) — mensajería asíncrona JS→nativo.

Es decir, Java inyecta `NativeInterface` en el runtime V8, React lo llama para pedir
datos de contenido/DB y manda eventos con `postMessage`. Toda la lógica de
presentación vive en JS; la de datos/cripto/almacenamiento en Java/nativo.

## Interés para ingeniería inversa

- Los bundles son JS minificado webpack **legible y modificable** (ya extraídos en
  `assets-extract/assets/webapp/`). Se pueden beautificar y analizar sin tocar DEX.
- `window.NativeInterface` es el **contrato exacto** entre web y app: enumerar sus
  métodos (buscando `NativeInterface.` en los bundles) da la API interna completa.
- No requieren motor nativo para análisis estático: `analyze-javascript-application`
  de REA o simple beautify + grep.

## Pendiente (opcional)
- [ ] Enumerar todos los métodos de `window.NativeInterface` (API web↔nativo).
- [ ] Beautificar `MepsBlaster.bundle.js` (el más pequeño y central) y documentar
      cómo transforma el contenido MEPS a DOM.
