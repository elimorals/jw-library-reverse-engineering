# Criptografía, firmas y PKI de contenido

## Librerías nativas cripto (×4 ABIs)

| .so | Propósito |
|---|---|
| `libsodium.so` + `libsodium-wrapper.so` | NaCl: firmas Ed25519 |
| `libargon2native.so` + `libargon2jni.so` | Argon2 (KDF desde contraseña/secreto) |
| (DEX) `com.google.crypto.*` (Tink, 69 clases) | AEAD / keysets |

## org.watchtower.dss.SodiumHelper — primitiva de verificación

`dss` = Digital Signing Service. Puente JNI a libsodium. Métodos:

```
cryptoSignInit() · cryptoSignUpdate() · cryptoSignFinalVerify()   (multi-parte)
cryptoSignVerifyDetached()                                        (detached)
```

Son `crypto_sign_*` de libsodium = **Ed25519 en modo verificación**. La app solo
**verifica** firmas (no firma): es un cliente que valida autenticidad/integridad
del contenido. Referenciada desde `pb/f` (envoltorio de verificación).

## PKI jerárquica de contenido — paquete `pb` (ofuscado R8)

El subsistema más interesante del binario. Implementa una **cadena de confianza
de dos niveles con revocación**, no un simple "firmar y verificar":

```
Root Key (raíz, embebida/confiada)
   └─ firma → Signing Key (clave de firma de contenido)
                 └─ firma → contenido (.jwpub / catálogo / media)
   + Listas de revocación firmadas (root y signing)
```

### Mapa de clases `pb/*`

| Clase | Rol (por sus símbolos) |
|---|---|
| `pb/c`, `pb/k` | Validadores de archivo firmado: `keyManager`, `trustedRootKey`, `streamReader`, `signature`, `type` |
| `pb/f` | Verificación base Ed25519: `message`, `data`, `signature`, `signingKey` → llama `SodiumHelper` |
| `pb/g` | Clave de firma: `signingKey`, `signingPublicKeySignature`, `revokedKeySignature`, `bytes` |
| `pb/h` | Revocación: `rootKeyRevocationList`, `signingKeyRevocationBlock`, `decodedTrustedRootKey`, `encodedSignature`, `saltPacket`, `revokedKeys`, `revokedKeySignatures` |
| `pb/d` | Estado de revocación: `getRevokedRootKeys`, `getRevokedSigningKeys`, `persistRootKeysAsRevokedCallback`, `persistSigningKeysAsRevokedCallback` |
| `pb/e` | `ResultPayload` (result/error) |
| `pb/a` | Base64 decoder propio |
| `pb/j` | **Enum de errores de validación** (ver abajo) |

### Errores de validación (`pb/j`) — la superficie de garantías

```
GeneralValidationError          InvalidBlockSize
InvalidFileSignature            InvalidIdentifyingMark
InvalidRootKeyRevocationList     InvalidSigningKeyRevocationBlock
InvalidSigningKeyRevocationListFilePath
InvalidSigningKeyRevocationListSignature
UntrustedSigningPublicKeySignature
AvailableRootKeyRevoked          (+ GeneralValidationError sin excepción → prohibido)
```

Cada archivo de contenido lleva una **marca identificadora** (`InvalidIdentifyingMark`)
y un **saltPacket** (sugiere cifrado/derivación además de firma: aquí entra Argon2).

### Enlace con el catálogo (con matiz)

`catalog.db` tiene la tabla `PublicationRootKey(Symbol, Year, Language, Id)` y
`Publication.PublicationRootKeyId` (FK). Las 4,625 publicaciones se agrupan por
"root key".

**Advertencia de interpretación:** el `PublicationRootKey` del catálogo es un
**identificador textual de la obra** (`Symbol` tipo `nwt`, `w`, `g` + idioma), **no**
he confirmado que sea material criptográfico Ed25519. La `trustedRootKey` que usa
el verificador (`pb/c`, `pb/k`, `pb/h`) está embebida como byte-array en el código
`pb`, no en esta tabla. Ambos usan la palabra "root key" pero pertenecen a capas
distintas (identidad de contenido vs. ancla de confianza cripto). No tengo
evidencia que los una directamente; queda como hipótesis a verificar.

## Algoritmo de verificación (decompilado, `pb.c.b`)

`pb.c.b(f verificador, h bloque, byte[] clave)` (ver `decompiled/pb.c.b_verify.java`):

```java
boolean b(f fVar, h hVar, byte[] bArr) {
    fVar.b(this.f46939b);                       // 1. update: prefijo/contexto
    byte[] e  = hVar.e();                        // bytes del bloque firmado
    byte[] s0 = l.s0(e, 0 .. e.length-64);       // 2. payload = todo menos 64 bytes
    byte[] pad = new byte[64];                    //    (64 = tamaño firma Ed25519)
    fVar.b(l.w(s0, pad));                         // 3. update: payload + padding de 64
    fVar.b(this.f46940c);                         // 4. update: sufijo/contexto
    return fVar.a(bArr, hVar.g());                // 5. finalVerify(clave, firma g())
}
```

Lectura: verificación **Ed25519 multi-parte** con **separación de dominio**
(dos contextos constantes `f46939b`/`f46940c` envuelven el payload). Los últimos
**64 bytes** del bloque son la firma. `f` envuelve a `SodiumHelper`
(`cryptoSignInit/Update/FinalVerify`). Confirma firma, no cifrado, en este paso.

## Dónde vive la clave raíz confiada — ENCONTRADA

No está en el paquete `pb`, pero sí **un nivel arriba**, como constante embebida en
`z9.a.<init>` (el validador de assets que construye `LibraryApplication$n.1`):

```java
public a(b keyRevocations, y8.a analytics) {
    this.f53128a = keyRevocations;   // lista de revocación (pb.d)
    this.f53129b = analytics;        // registra fallos de validación (interno)
    this.f53130c = "9SBJdes0kGnd70KqaFYaV/DynBtJGvCk0EvfxWtJrBg=";  // trustedRootKey
}
```

**trustedRootKey (ancla de confianza raíz), verificada:**
- Base64: `9SBJdes0kGnd70KqaFYaV/DynBtJGvCk0EvfxWtJrBg=`
- Decodifica a **exactamente 32 bytes = clave pública Ed25519**
- hex: `f5204975eb349069ddef42aa68561a57f0f29c1b491af0a4d04bdfc56b49ac18`

`z9.a.b(stream, assetName)` crea el validador `pb.c(keyManager)` y verifica el
asset en streaming contra esta raíz, comprobando revocación. Si falla, lo registra
en `analytics` (`y8.a`, telemetría **interna**, no App Center — ver doc 08).

> Corrección respecto a una nota previa: sí existe una root key hardcodeada; estaba
> en `z9.a`, no en `pb` (donde solo busqué al principio).

## Cifrado de contenido (confidencialidad / DRM) — paquete `ob`

Además de firmar, parte del contenido va **cifrado**. Cadena (paquete `ob`):

```
token/seed (Base32-Crockford "0123456789ABCDEFGHJKMNPQRSTVWXYZ")
   → ob.d.a: parsea 1er byte = versión(bits0-3) + flags(bit4, bits5-7) + payload
   → ob.b.a: Argon2id(password=configParams(+id 4B big-endian), salt=saltPacket,
                       t = b()*10+5, p = 2, outLen = 32, v1.3)  → clave 256 bits
   → XChaCha20(-Poly1305)  (impl. D5.l / K5.x, sobre libsodium)  → descifra
```

- **KDF**: Argon2id (`com.lambdapioneer.argon2kt` → `libargon2`), salida 32 bytes.
- **AEAD**: **XChaCha20-Poly1305** (3 refs `XChaCha20`; clave de 32 B encaja).
- **salt**: el `saltPacket` que vimos en `pb/h` acompaña al contenido firmado.
- La clave **se deriva, no se almacena** → cifrado ligado a la publicación/token.

`com.google.crypto.tink` está **empaquetado pero no referenciado** desde código
`org.jw`; el cifrado efectivo usa el camino libsodium/XChaCha20, no Tink.

## Lectura de seguridad

- Diseño **robusto**: PKI de 2 niveles + CRL firmada + verificación Ed25519 nativa
  es muy superior a "confiar solo en TLS". Protege contra CDN comprometido y
  contenido manipulado en reposo.
- El `saltPacket` + Argon2 + Tink apuntan a que parte del contenido va **cifrado**
  (no solo firmado), coherente con acuerdos de licencia de medios.

## Resuelto
- [x] Root key confiada extraída (Ed25519, hex `f52049…ac18`, en `z9.a`).
- [x] AEAD = XChaCha20-Poly1305 (libsodium), no Tink (empaquetado pero sin usar).
- [x] `saltPacket` → Argon2id → clave 256 bits → descifrado (paquete `ob`).

## Pendiente (opcional)
- [ ] Origen del `token/seed` que alimenta Argon2 (¿descarga por publicación / cuenta?).
