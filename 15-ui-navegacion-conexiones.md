# UI, pantallas, navegación y conexiones

## Arquitectura de UI: MVVM híbrido

La app mezcla **Jetpack Compose + Vistas clásicas (AppCompat/databinding)** y añade
los **paneles de contenido en React/V8** (doc 11). Patrón **MVVM**: cada pantalla es
un `*Page` con su `*ViewModel`.

### Volumen de UI

| Métrica | Valor |
|---|---|
| Layouts XML | 298 |
| Drawables | 3,062 |
| Idiomas (`values-*/strings.xml`) | **147** |
| Strings (tabla por defecto) | 1,177 |
| ViewModels (top-level) | 36 (+248 clases en `view/library/viewmodel`) |

147 idiomas confirma el alcance multilingüe MEPS (coherente con las fuentes Noto
multi-script de la webapp, doc 04).

### Pantallas principales (`org.jw.jwlibrary.mobile.view.*`)

| Pantalla | Área |
|---|---|
| `LibraryPage` | Biblioteca (home) |
| `PublicationCategoryPage` | Categorías de publicaciones |
| `AudioCategoryPage`, `VideoCategoryPage` | Media por categoría |
| `PeriodicalYearsPage` | Revistas por año |
| `ConventionDaysPage`, `ConventionReleasesPage` | Asambleas |
| `bible/pages/BibleTocPage` | Índice de la Biblia |
| `MeetingsPage` | Reuniones |
| `SearchPage` (+`SearchPageViewModel`) | Búsqueda |
| `ComposeCompatPage` | Puente Compose↔Vistas |

Subáreas por volumen: `library/viewmodel` (248), `meetings/viewmodel` (42),
`library/pages` (40), `progress` (39), `bible/viewmodels` (25), `filmstrip` (13),
`accessibility` (13), `toc` (10), `imagesource` (11).

### Navegación
- `MainActivity` (LAUNCHER) y `SiloContainer` (contenedor principal, `singleTask`).
- `BottomNavigationView` como navegación raíz (visto en `SiloContainer.onCreate`,
  doc 03): Biblioteca / Reuniones / Media / etc.
- Ajustes: `Settings` (PreferenceScreen). Diálogos: coaching tips, Terms of Use,
  Privacy Acceptance (manifiesto, doc 01).

## Conexiones (capa de red)

| Elemento | Detalle |
|---|---|
| Cliente HTTP | **okhttp3** (reducido por R8) sobre HTTPS |
| API media | `/apis/pub-media/GETPUBMEDIALINKS` → `app.jw-cdn.org` (doc 09) |
| Contenido online | `wol.jw.org`, `www.jw.org` |
| Compartir | `org.jw.service.web.ShareFinder` genera links `www.jw.org/finder` |
| TLS | HTTPS forzado salvo `dev-library.jw.org`; sin pinning (doc 05) |
| Integridad | firma Ed25519 E2E del contenido (doc 02) |

## Accesibilidad
Paquete `view/accessibility` (13 clases) + evento bridge `restoreAccessibilityFocus`
(doc 07) → soporte de lectores de pantalla en el contenido React.

## Wear OS
`android.support.wearable.complications.*` (ComplicationData/Text) +
`uses-library com.google.android.wearable` (manifiesto). Expone **complications**
(texto del día/versículo) a esferas de reloj Wear OS. Recursos `drawable-watch`.
