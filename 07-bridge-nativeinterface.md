# Puente JS ↔ Java: protocolo NativeBridge

La webapp React (en Javet/V8) y el lado nativo Android se comunican por un **bus de
mensajes tipados**. El emisor JS es `NativeBridgeSender.send({type, ...payload})`,
que por debajo usa `postMessage(...)` hacia el host V8. El nombre
`window.NativeInterface.send` del doc 04 es el punto de entrada; el objeto interno
minificado es `NativeBridgeSender`.

## JS → Nativo (54 tipos de mensaje)

Volumen: PrimaryPane y StudyPane concentran 75 `send` cada uno; NoteEditor 23,
BreakingNews 21, TermsOfUse 22, ImageDisplay 19.

### `NATIVE_ACTION_*` — comandos (el JS pide algo al nativo)

| Grupo | Mensajes |
|---|---|
| Navegación | `NAVIGATE`, `NAVIGATE_ONLINE_LINK`, `NAVIGATE_TAG` |
| Solicitud de contenido | `REQUEST_PRIMARY_CONTENT`, `REQUEST_STUDY_CONTENT`, `REQUEST_NOTE_EDITOR_CONTENT`, `REQUEST_IMAGE_CONTENT`, `REQUEST_MEDIA`, `REQUEST_MARGINAL`, `REQUEST_SUPPLEMENTARY_NOTES`, `REQUEST_PUBLICATION_REFERENCE`, `REQUEST_SINGLE_PUBLICATION_REFERENCE`, `REQUEST_BREAKING_NEWS`, `REQUEST_TERMS_OF_USE`, `REQUEST_STUDY_PANE_NAVIGATE_BACK` |
| Persistencia | `PERSIST_NOTE`, `PERSIST_USER_MARK`, `UPDATE_INPUT_FIELDS`, `UPDATE_EXPANDED_DOCUMENT_SECTIONS`, `UPDATE_SCROLL_POSITION`, `UPDATE_TOP_ELEMENT` |
| UI / menús | `SHOW_IMAGE`, `SHOW_LOUPE`/`HIDE_LOUPE`, `SHOW_USER_MARK_MENU`/`HIDE_USER_MARK_MENU`/`SHOW_EDIT_USER_MARK_MENU`, `SHOW_PARAGRAPH_MENU`, `SHOW_NOTE_OPTIONS`, `SELECT_MULTIMEDIA` |
| Notas (modo edición) | `NOTE_EDIT_MODE_ENTERED`, `NOTE_EDIT_MODE_EXITED` |
| Research Guide | `DOWNLOAD_RESEARCH_GUIDE`, `DISMISS_RESEARCH_GUIDE_DOWNLOAD_BOX` |
| Paginación (Android) | `ANDROID_LOCK_PAGING`, `ANDROID_UNLOCK_PAGING` |
| Términos de uso | `TERMS_OF_USE_PAGE_REACHED_BOTTOM` |

### `NATIVE_EVENT_*` — notificaciones (el JS avisa de un hecho)

`PRIMARY_IMAGE_TAPPED`, `IMAGE_CONTENT_LOADED`, `STUDY_CONTENT_LOADED`,
`TEXT_SELECTION_CREATED`, `USER_MARK_DRAWN`, `SPEEDBAR_SHOWN`/`SPEEDBAR_HIDDEN`.

### `SELECT_*` — selección de elementos de contenido

`SELECT_FOOTNOTE`, `SELECT_ENDNOTE`, `SELECT_MARGINAL`, `SELECT_ALL_MARGINALS`,
`SELECT_NOTE`, `SELECT_COMMENTARY`, `SELECT_VERSE_LABEL`, `SELECT_PARAGRAPH_LABEL`,
`SELECT_OUTLINE`. Payloads típicos: `{footnoteID|endnoteID|noteGuid|verse|paragraph,
scroll:true}`.

## Nativo → JS (eventos DOM custom que Java dispara sobre la webapp)

| Evento | Significado |
|---|---|
| `clearContentBasedCache` | Invalidar caché de contenido renderizado |
| `clearTextSelection` | Deseleccionar texto |
| `persistNoteEditorContent` | Forzar guardado del editor de notas |
| `restoreAccessibilityFocus` | Restaurar foco de accesibilidad |
| `willResize` | Aviso previo a reajuste de layout |

(Además de eventos DOM estándar: click, touch*, scroll, resize, composition*, etc.)

## Lectura

- El contrato es **asimétrico y rico**: el JS maneja presentación/interacción y
  delega al nativo **todo** acceso a datos (contenido, media, notas, marcas) y
  acciones de sistema (navegación, descargas, menús nativos). Coherente con la PKI:
  el contenido sensible nunca se resuelve en JS, solo se pinta.
- Superficie: quien controle el canal `postMessage` (p. ej. contenido inyectado en
  el render) podría emitir `NATIVE_ACTION_*`. El riesgo lo acota que el contenido
  renderizado proviene de publicaciones **verificadas por firma** (doc 02).

## Artefactos
Bundles en `assets-extract/assets/webapp/*.bundle.js` (minificados, analizables).
