# Datos y almacenamiento

## Motores de persistencia

| Motor | .so / lib | Uso |
|---|---|---|
| SQLite (requery/sqlite-android) | `libsqlite3x.so`, `io.requery.android.*` | Catálogo y datos de usuario |
| Realm | `librealm-jni.so`, `io.realm.*` (47 clases internas) | Base de objetos (estado de app / media) |

## Bases de datos en assets

| Archivo | Tamaño | Contenido |
|---|---|---|
| `assets/catalog.db` | 13.3 MB | Catálogo de publicaciones (semilla de fábrica) |
| `assets/mepsunit.jwdat` | 9.9 MB | Datos MEPS (unidades lingüísticas, idiomas, mapeo) |
| `assets/v2..v16_upgrade.sql` | — | Migraciones de esquema de la BD de usuario (16 versiones) |

## Esquema de catalog.db (12 tablas)

```
Publication            PublicationAsset         PublicationDocument
PublicationRootKey     PublicationAttribute     PublicationAttributeMap
PublicationAssetImageMap   ImageAsset           CuratedAsset
AvailableBibleBook     DatedText                Revision
```

Hechos:
- **`Publication`: 4,625 filas** · **`PublicationAsset`: 4,625 filas**.
- `Publication(PublicationRootKeyId, MepsLanguageId, PublicationTypeId,
  IssueTagNumber, Title, Symbol, KeySymbol, Year, …)`, PK `Id`,
  UNIQUE `(KeySymbol, IssueTagNumber, MepsLanguageId)`.
- `PublicationRootKey(Symbol, Year, Language, Id)` — identidad canónica de la obra.
- `AvailableBibleBook`, `DatedText` → contenido bíblico y textos diarios.

## Datos de usuario — esquema de `userData.db`

Tablas confirmadas desde los scripts de migración (`v2..v16_upgrade.sql`):

| Tabla | Columnas clave | Contenido |
|---|---|---|
| `UserMark` | UserMarkId, ColorIndex, StyleIndex, LocationId, UserMarkGuid, Version | **Resaltados** del usuario |
| `Tag` | TagId, Type, Name | **Etiquetas / favoritos** |
| `Location` | +Specialty, Edition, Title | Referencia a un punto de contenido (publicación/documento) |
| `InputField` | — | Campos rellenables (lecciones/estudio) |
| `PlaylistItem` | — | Elementos de listas de reproducción |

Las migraciones usan el patrón *copiar-a-tabla-nueva-y-renombrar* (p. ej.
`UserMark`→`OldUserMark`→nueva `UserMark`). `userData.db` NO viene en el APK: se
crea en el dispositivo (esquema por `io.requery`). `catalog.db` sí es semilla.

- **`userData.db`** es la **única BD incluida en respaldos** (`backup_rules.xml` →
  `<include domain="database" path="userData.db">`). Guarda resaltados, etiquetas,
  notas, listas y progreso. Es el dato personal del usuario.
- `allowBackup="true"`: el respaldo Auto Backup de Android sube `userData.db` a la
  nube del usuario. Observación de privacidad menor (notas personales en backup).

## FileProvider y rutas

`filepaths.xml`:
```xml
<external-path name="backups" path="Android/data/org.jw.jwlibrary.mobile/files/backups" />
<root-path     name="root"    path="/" />
```

- `backups`: ruta de respaldos manuales del usuario (export/import de datos).
- **`root-path "/"`**: el FileProvider puede generar `content://` para **cualquier
  ruta del sistema**. Es una mala práctica reconocida (fuga potencial de archivos
  vía URIs concedidas). Mitigado por `exported=false` y `grantUriPermissions`, pero
  conviene acotarlo a los directorios que realmente se comparten.

## Pendiente (opcional)
- [ ] Volcar esquema de `userData.db` desde el último `vN_upgrade.sql`.
- [ ] Revisar si Realm cifra su archivo (clave vía Argon2) o va en claro.
