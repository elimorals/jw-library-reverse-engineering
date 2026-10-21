# Webapp: paneles React y lado Java del bridge

Complementa el doc 07 (protocolo de 54 mensajes). Aquí: la lógica de render JS y
las **contrapartes Java** que reciben/emiten cada mensaje.

## Paneles (JS ↔ Java emparejados)

Cada bundle React (`assets/webapp/*.bundle.js`) tiene un controlador Java en
`org.jw.jwlibrary.mobile.webapp.*`:

| Panel React (JS) | Controlador Java | Función |
|---|---|---|
| `PrimaryPane.bundle.js` | `StudyContentWebApp` / primary | Lectura principal del documento |
| `StudyPane.bundle.js` | `StudyContentWebApp` | Panel de estudio (notas, refs cruzadas) |
| `NoteEditor.bundle.js` | `NoteEditorWebView` | Editor de notas |
| `BreakingNews.bundle.js` | `BreakingNewsWebView` | Avisos |
| `TermsOfUse.bundle.js` | `TermsOfUseWebView` | Términos de uso |
| `ImageDisplay.bundle.js` | (webapp image) | Visor de imágenes |
| `MepsBlaster.bundle.js` | (render MEPS→DOM) | Transforma contenido MEPS a HTML |

## Receptor del bridge en Java

- Clase despachadora: **`org.jw.jwlibrary.mobile.webapp.x0`** (parser/serializer de
  mensajes del bridge; métodos `a`/`b`).
- **50 tipos `NATIVE_ACTION_*` / `NATIVE_EVENT_*` están referenciados en el DEX Java**
  del paquete `webapp` — es decir, Java parsea el `type` del mensaje y despacha. Hay
  **simetría casi total** con los 54 tipos emitidos por el JS (doc 07).
- Dirección Java→JS: Java ejecuta JS en el contexto V8 (Javet) y dispara los eventos
  DOM custom (`clearContentBasedCache`, `persistNoteEditorContent`, etc., doc 07).

## Arquitectura de ejecución (confirmada)

```
Java (datos/cripto/DB)
   ├─ Javet ─ V8 (contexto JS por panel)
   │    └─ React bundle (presentación)
   │          └─ NativeBridgeSender.send({type,…})  ──► x0 (Java) ──► servicio/DB
   └─ ejecuta JS / dispara eventos DOM custom  ◄── respuestas y comandos
```

El JS **nunca** toca disco, red ni cripto: pide todo por el bridge. Coherente con la
PKI (el contenido se verifica/descifra en Java y al JS solo llega HTML a pintar).

## Render MEPS (`MepsBlaster`)
El bundle más pequeño (0.31 MB) y central: recibe el contenido MEPS resuelto por Java
y lo transforma en el DOM que los demás paneles muestran. (Lógica interna minificada
no beautificada aquí; los bundles están en `assets-extract/` para inspección.)
