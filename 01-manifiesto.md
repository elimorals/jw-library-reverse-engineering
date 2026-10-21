# Manifiesto y superficie de ataque

Manifiesto decodificado completo: `res-decoded/AndroidManifest.xml`.

## Permisos (10)

| Permiso | Nota |
|---|---|
| INTERNET, ACCESS_NETWORK_STATE, ACCESS_WIFI_STATE | Red |
| WAKE_LOCK | Mantener CPU despierta (descargas/reproducción) |
| FOREGROUND_SERVICE + FOREGROUND_SERVICE_MEDIA_PLAYBACK | Servicio de audio en primer plano |
| READ_EXTERNAL_STORAGE (maxSdk 32), WRITE_EXTERNAL_STORAGE | Almacenamiento legacy |
| READ_MEDIA_AUDIO | Android 13+ (importar audio) |
| `org.jw.jwlibrary.mobile.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` | Firma, interno |

Sin permisos peligrosos (ubicación, contactos, cámara, micrófono, SMS). Perfil
de privacidad conservador. `requestLegacyExternalStorage=true` y
`preserveLegacyExternalStorage=true` (compatibilidad con almacenamiento clásico).

`android:allowBackup="true"` → respaldos solo de `userData.db` (ver `backup_rules.xml`).

## Flags de aplicación notables

- `android:name=org.jw.jwlibrary.mobile.LibraryApplication` (Application class)
- `largeHeap="true"` (V8 + Realm + imágenes consumen memoria)
- `hardwareAccelerated="true"`, `enableOnBackInvokedCallback="true"` (Android 13+)
- `networkSecurityConfig=@xml/network_security_config` (ver doc 05)
- `appComponentFactory=androidx.core.app.CoreComponentFactory`

## Componentes EXPORTADOS (superficie de ataque)

| Componente | Tipo | Por qué está expuesto |
|---|---|---|
| `MainActivity` | activity | LAUNCHER |
| `activity.SiloContainer` | activity | **Deep links + VIEW/SEND de media + SEARCH** (ver doc 03) — el punto más expuesto |
| `media.AudioService` | service | `MediaBrowserService` + `MEDIA_BUTTON` (control multimedia del sistema/Android Auto) |
| `trimmer.Trimmer` | activity | Editor de recortes de video (singleTask) |
| `androidx.compose.ui.tooling.PreviewActivity` | activity | **Residuo de tooling de Compose** — exported, sin uso en producción |
| `profileinstaller.ProfileInstallReceiver` | receiver | Protegido por permiso `android.permission.DUMP` (solo shell/sistema) |

### Componentes NO exportados (internos)
- `FileProvider` (authorities `org.jw.jwlibrary.mobile.fileprovider`), `exported=false`
  pero con `filepaths.xml` que expone `root-path "/"` (ver doc 05 — mala práctica).
- Actividades de media/notas/imágenes/ajustes/coaching, `PipPlayerActivity`
  (proceso `:pip`), `PipMediaService`, `DownloadService` (dataSync),
  `ImportService` (BIND_JOB_SERVICE).

## Observaciones de superficie

1. **`PreviewActivity` exportada**: clase de depuración de Jetpack Compose que no
   debería ir en release. Riesgo bajo (no hace nada sensible), pero es higiene.
2. **`SiloContainer`** acepta `mimeType="*/*"` con esquemas `content://` y `file://`
   más deep links `jwlibrary://`, `jwlplaylist://` y App Links a `www.jw.org/finder`.
   Es la entrada principal de datos externos → foco de revisión (doc 03).
3. **`AudioService` exportado** con `MediaBrowserService`: normal para apps de
   audio; otras apps pueden enumerar/controlar la reproducción vía MediaBrowser.
