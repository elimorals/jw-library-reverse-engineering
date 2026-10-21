# Librerías nativas (.so) — desensamblado e identificación

4 ABIs: `arm64-v8a`, `armeabi-v7a`, `x86`, `x86_64`. Todas las .so por ABI:

| .so | Origen | Función | Verificado |
|---|---|---|---|
| `libsodium-wrapper.so` | **Watchtower (custom)** | JNI Ed25519 para `SodiumHelper` | ✅ símbolos |
| `libsodium.so` | libsodium (NaCl) | Cripto: firmas, XChaCha20, secretbox | terceros |
| `libargon2jni.so` + `libargon2native.so` | lambdapioneer/Argon2Kt + ref | KDF Argon2id | terceros |
| `librealm-jni.so` | Realm | Base de datos de objetos | terceros |
| `libjavet-v8-android-i18n.v.4.1.5.so` | Javet 4.1.5 | Motor V8 + ICU | terceros |
| `libsqlite3x.so` | requery/sqlite-android | SQLite nativo | terceros |

## libsodium-wrapper.so (la única .so propia) — desensamblada

Tamaño: ~6 KB (x86_64). Es un **wrapper JNI fino** sobre `libsodium.so`. Símbolos
exportados (de `strings`/tabla de símbolos):

```
Java_org_watchtower_dss_SodiumHelper_cryptoSignInit
Java_org_watchtower_dss_SodiumHelper_cryptoSignUpdate
Java_org_watchtower_dss_SodiumHelper_cryptoSignFinalVerify
Java_org_watchtower_dss_SodiumHelper_cryptoSignVerifyDetached
→ delegan a: crypto_sign_init / _update / _final_verify / _verify_detached
```

**Confirmación binaria**: las primitivas son `crypto_sign_*` de libsodium = **Ed25519
multi-parte** (init/update/final_verify) y **detached** (verify_detached). Coincide
exactamente con el análisis estático del DEX (doc 02). No hay más lógica propia en
nativo; la cadena de confianza (PKI, revocación) vive en Java (`pb`, `z9.a`) y solo
las primitivas criptográficas bajan a C.

## Las demás .so
Son dependencias de terceros bien conocidas, usadas vía su JNI estándar:
Realm (persistencia), Javet/V8 (ejecución JS), Argon2 (KDF), SQLite (catálogo/datos).
No requieren desensamblado para entender su rol; sus contratos son públicos.

Artefactos: `.so` extraídas en `so-extract/` (arm64) y `so-extract64/` (x86_64).
