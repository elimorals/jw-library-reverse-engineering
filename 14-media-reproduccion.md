# Reproducción de media, notas y marcas

## Motor de reproducción: ExoPlayer

- **ExoPlayer** (`com.google.android.exoplayer2`, **339 clases**) + `androidx.media`
  (11) es el reproductor de audio y video.
- `org.jw.jwlibrary.mobile.media.*`: `AudioService`, `viewmodels` (`PlayableItem`,
  `MediaPlaylistViewModel`), `generators` (`MediaHistory`,
  `MediaPlaylistViewModelGenerator`), `utils` (`MediaCategoryPlaybackHelper`).

## AudioService (MediaBrowserService)

- Declarado `exported=true`, `foregroundServiceType=mediaPlayback`, con intent-filters
  `android.media.browse.MediaBrowserService` y `MEDIA_BUTTON` (manifiesto, doc 01).
- Métodos de ciclo de vida: `onCreate`, `onBind`, `onStartCommand`, `onTaskRemoved`,
  `onDestroy`, `stopService` (+ helpers `a..p`).
- Al ser `MediaBrowserService`, se integra con **Android Auto / Wear / controles del
  sistema**: otras apps del sistema pueden navegar el árbol de media y controlar la
  reproducción. Reproducción en primer plano con notificación de media.

## Picture-in-Picture (video)

- `PipPlayerActivity` corre en **proceso aparte `:pip`** (manifiesto), con
  `supportsPictureInPicture=true` y tema `Theme.Jwl.Pip`.
- `PipMediaService` (`org.jw.jwlibrary.mobile.pip`) coordina el PiP.
- `MediaPlayerActivity` es el reproductor a pantalla completa.

## Editor de notas y marcas de usuario

- `NoteEditorActivity` + `NoteEditorWebView` (panel React `NoteEditor.bundle.js`).
- Mensajes del bridge (doc 07): `PERSIST_NOTE`, `NOTE_EDIT_MODE_ENTERED/EXITED`,
  `PERSIST_USER_MARK`, `SHOW_USER_MARK_MENU`/`SHOW_EDIT_USER_MARK_MENU`,
  `USER_MARK_DRAWN`, evento `persistNoteEditorContent`.
- Persistencia en `userData.db`: tablas `UserMark` (resaltados: ColorIndex,
  StyleIndex, LocationId, GUID) y `Tag` (favoritos) — doc 06.

## Editor de video
`org.jw.jwlibrary.trimmer.Trimmer` (activity exportada, `singleTask`) — recorte de
video para compartir clips.
