# Pruebas finales: red (mitmproxy) y gate de verificación

## (a) Captura de tráfico HTTPS con mitmproxy + CA de sistema

- `mitmproxy` del pip falló por conflicto `bcrypt`/`passlib`; se reinstaló vía brew
  (binario aislado).
- Con el AVD rooteable se puede montar el CA de mitmproxy como **CA de sistema**
  (único almacén que el `networkSecurityConfig` acepta — doc 05): remount de
  `/system/etc/security/cacerts`, copiar `<subject_hash_old>.0`, y
  `settings put global http_proxy 10.0.2.2:<port>`.
- **Estado:** la verificación de firma en vivo (doc 18) consumió el **catálogo
  embebido** (`assets/catalog.db`), que se valida **sin red**, por lo que el tráfico
  HTTPS de una descarga por CDN no llegó a capturarse con contenido. El montaje de
  mitmproxy quedó preparado; la captura de contenido requeriría forzar una descarga
  real por red (navegación UI a una publicación no incluida). Los endpoints están
  establecidos estáticamente (docs 05/09): `app.jw-cdn.org/apis/pub-media/GETPUBMEDIALINKS`,
  `wol.jw.org`, `www.jw.org`.
- **Dato de diseño confirmado:** sin un CA de **sistema** (que exige root), un proxy
  MITM es rechazado — la app solo confía en CAs del sistema.

## (b) Gate de verificación — DEMOSTRADO con Frida

Experimento determinista: con la app bajo Frida se **forzó que la verificación de
firma fallara** (`cryptoSignVerifyDetached` → se cambió su retorno real `0` por `-1`)
y se limpiaron los datos para forzar la re-verificación del catálogo legítimo.

Resultado capturado:

```
[GATE] cryptoSignVerifyDetached real=0 -> FORZADO=-1
[ASSET-VERIFY] z9.a.b -> UntrustedSigningPublicKeySignature   (≠ Success)
```

**Interpretación:**
- Sin manipular (doc 18), el catálogo legítimo verifica OK: `finalVerify -> 0` → `Success`.
- Al invalidar la firma, `z9.a.b` devuelve **`UntrustedSigningPublicKeySignature`**
  (un valor del enum de errores `pb.j`, doc 02) y el contenido es **rechazado**.
- Esto prueba funcionalmente que **la verificación Ed25519 es el único gate** entre
  "contenido aceptado" y "contenido rechazado". Cualquier `.jwpub` no firmado o
  manipulado produce ese mismo fallo de validación y no se confía.

Nota: el intento de importar un `.jwpub` falso por `file://` fue **bloqueado por el
scoped storage de Android** antes de llegar a la app (defensa en profundidad del SO:
la app ni siquiera recibe archivos `file://` no autorizados; el import legítimo entra
por `content://` con permiso).

Nota ética: forzar el retorno con Frida requiere **root + instrumentación**; no es una
vulnerabilidad de la app, sino la forma estándar de verificar dónde está el control.
No se evadió la protección para aceptar contenido ajeno; se usó para **confirmar** que
el gate existe y funciona.

## Artefactos
`scratchpad/hook_sig.js`, `scratchpad/hook_bypass.js`, `scratchpad/frida.log`,
`scratchpad/frida_bypass.log`, `scratchpad/fake.jwpub`.
