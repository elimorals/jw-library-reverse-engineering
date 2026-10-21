# Instrumentación en vivo con Frida

Frida **17.24.3** (host) + `frida-server` arm64 en el AVD rooteable. La app se lanzó
con `frida -U -f org.jw.jwlibrary.mobile -l hook_sig.js` (spawn). Script en
`scratchpad/hook_sig.js`.

## Hooks instalados (confirmado)

```
[+] SodiumHelper hooked        (org.watchtower.dss.SodiumHelper.cryptoSign*)
[+] pb.c.b hooked              (verificador de bloque)
[+] P5.a.b (Argon2) hooked     (KDF)
[+] z9.a.b hooked              (verificación de asset con trustedRootKey)
```

## Verificación de firma CAPTURADA EN VIVO

Al disparar la descarga de contenido ("Download Examining the Scriptures—2026"),
los hooks registraron la ejecución real de la verificación Ed25519:

```
[SIG] SodiumHelper.cryptoSignInit          -> 0      (1×)
[SIG] SodiumHelper.cryptoSignUpdate        -> 0    (234×)   ← stream por bloques
[SIG] SodiumHelper.cryptoSignFinalVerify   -> 0      (1×)
[SIG] SodiumHelper.cryptoSignVerifyDetached-> 0      (1×)
```

**Esto confirma dinámicamente la cadena cripto del doc 02:**
- La verificación es **Ed25519 multi-parte**: `init` → 234× `update` (el contenido se
  alimenta por bloques en streaming) → `finalVerify`.
- Además una verificación **detached** (`cryptoSignVerifyDetached`) sobre una firma
  separada (coherente con la separación payload/firma de 64 bytes de `pb.c.b`).
- Todo pasa por `libsodium` vía el JNI `SodiumHelper` (doc 12), exactamente como
  predijo el análisis estático.

> Los retornos `-> 0` son el valor nativo de las funciones JNI (0 = OK en la
> convención de estas envolturas); el contenido descargado se aceptó como auténtico.

## Lo que esto demuestra

El análisis estático (docs 02/12) predijo la arquitectura de verificación; la
instrumentación **lo confirma ejecutándose sobre contenido real**: JW Library valida
criptográficamente cada pieza de contenido que descarga, por streaming, contra su
ancla de confianza Ed25519 embebida (`z9.a`, hex `f52049…ac18`).

## Capacidad disponible (no ejecutada)
Con los hooks activos se puede además:
- Forzar `pb.c.b`/`cryptoSignVerifyDetached` a devolver `true` para probar si un
  `.jwpub` manipulado sería aceptado (prueba de bypass de verificación).
- Volcar los argumentos (clave pública, firma, payload) de cada verificación.
No se hizo para no alterar el comportamiento; el objetivo era observar, no evadir.

Artefactos: `scratchpad/hook_sig.js`, `scratchpad/frida.log`.
