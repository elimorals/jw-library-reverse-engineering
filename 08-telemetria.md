# Telemetría y analytics

> **ACTUALIZACIÓN con análisis dinámico (root) — corrige lo de abajo.** En runtime,
> App Center **sí se inicializa**: crea `shared_prefs/AppCenter.xml` con un `installId`
> (GUID `0045d1f7-8b85-4027-b670-01eaf00e1eb5`) y registra una sesión, más la DB
> `databases/com.microsoft.appcenter.persistence`. **Pero la tabla `logs` tiene 0
> eventos** tras haber pulsado **DECLINE** en el diálogo de privacidad. Conclusión
> corregida: App Center **arranca a nivel de librería pero respeta el opt-out** — no
> acumula ni envía eventos de telemetría sin consentimiento. Mi grep estático de
> `AppCenter.start` dio 0 (la init está ofuscada o usa otra ruta que no detecté);
> **la evidencia dinámica supera a la estática aquí.**

## Microsoft App Center — análisis ESTÁTICO (ver corrección arriba)

La librería está en el APK (`com.microsoft.appcenter.*`: 17 clases `crashes` + 11
`analytics`), pero **no hay evidencia de que se inicialice ni se use**:

| Comprobación | Resultado |
|---|---|
| `AppCenter.start / configure / configureAndStart` en todo el APK | **0 llamadas** |
| `LibraryApplication` menciona AppCenter | **No** |
| Código `org.jw.*` que referencia `com.microsoft.appcenter` | **Ninguno** |
| App Secret (GUID) de App Center asociado a `start()` | **No encontrado** |

Conclusión: **App Center es una dependencia transitiva arrastrada, no telemetría
activa.** Los endpoints `in.appcenter.ms` y `mobile.events.data.microsoft.com` que
aparecen en strings son **constantes por defecto dentro de la propia librería**, no
prueba de envío de datos. (Los GUID hallados en `res/` no se vinculan a un `start`
de App Center; no los atribuyo a telemetría.)

> Límite honesto: el análisis es estático. No puedo descartar una inicialización por
> reflexión o por una ruta no detectada, pero **no hay ningún rastro** de ella y la
> ausencia de App Secret + de `start()` es consistente con telemetría desactivada.

## Analytics interno (`y8.a`)

Existe un **analytics propio** (interfaz `y8.a`), inyectado por el grafo DI
(`J8.c` / `get().getInstance(Analytics::class.java)`), independiente de App Center.
Uso observado: el validador de firmas `z9.a.b` lo invoca para **registrar fallos de
validación de contenido** (`analytics.i(assetName, size)` cuando una firma no es
`Success`). Es telemetría de integridad/errores, de alcance interno.

## Permisos de red y privacidad (contexto)

- Permisos: solo `INTERNET`/`ACCESS_NETWORK_STATE`/`ACCESS_WIFI_STATE`. Sin
  identificadores de publicidad ni SDKs de ads/tracking de terceros detectados.
- Perfil coherente con una app sin publicidad ni monetización por datos.

## Pendiente (opcional)
- [ ] Enumerar los eventos concretos que `y8.a` emite y su destino (¿endpoint jw.org
      de métricas, o solo logging local?). Ver `org.jw.service.metrics`.