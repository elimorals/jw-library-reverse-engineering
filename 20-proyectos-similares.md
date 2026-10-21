# Proyectos similares (estado del arte)

Búsqueda web (2026-10) de trabajo público relacionado. **No existe un proyecto
público que haga ingeniería inversa del APK de JW Library** con análisis de su firma
de contenido, PKI o cifrado. Lo que hay son parsers del formato y clientes
alternativos:

| Proyecto | Qué hace | Relación con este análisis |
|---|---|---|
| [github.com/topics/jwpub](https://github.com/topics/jwpub) | Colección de repos sobre `.jwpub` | Parsers de formato, no análisis del APK |
| [MrCyjaneK/jwapi](https://github.com/MrCyjaneK/jwapi) | Cliente FOSS que usa la API de jw.org directamente | Alternativa de cliente; no toca el binario ni la cripto |
| [eliadca/JWPUBLICATIONS](https://github.com/eliadca/JWPUBLICATIONS) | Convierte HTML → JWPUB | Generación de contenido, no RE |
| `sws2apps/meeting-schedules-parser` | Parsea JWPUB/EPUB de guías de reunión | Lee contenido de publicaciones ya descifradas |
| `erykjj/jwpublib` | Publicaciones de referencia de dominio público en JWPUB | Contenido, no análisis |
| [Hilo en foro Sumatra PDF](https://forum.sumatrapdfreader.org/t/why-no-support-for-jw-orgs-jwpub-database-format/2891) | Discusión: "JWPUB es EPUB modificado / tipo base de datos" | Opinión comunitaria, no documentación técnica |

## Qué aporta este trabajo que no está público

1. **PKI de contenido de 2 niveles con revocación** (root → signing keys, CRL firmada)
   documentada desde el binario (`org.watchtower.dss` + paquete `pb`).
2. **Ancla de confianza Ed25519 extraída** (hex `f52049…ac18`, en `z9.a`).
3. **Esquema de cifrado de contenido** mapeado: token Base32-Crockford → Argon2id →
   XChaCha20-Poly1305 (paquete `ob`), confirmado a nivel binario (`libsodium-wrapper.so`).
4. **Verificación de firma confirmada EN VIVO** con Frida (Ed25519 multi-parte por
   streaming) y **gate de validación demostrado** (forzar el fallo → rechazo).
5. **Protocolo del bridge Javet/V8 ↔ Java** (54 mensajes) y arquitectura React.
6. **Postura de seguridad**: solo CAs de sistema (MITM requiere root), firma APK v2/v3
   verificada, telemetría opt-in que respeta el opt-out, DBs sin cifrar en reposo.

En resumen: los proyectos públicos trabajan **sobre** el formato `.jwpub` (parsear
contenido ya accesible); este análisis documenta **cómo el APK protege ese contenido**
(firma, cifrado, confianza), que no estaba documentado públicamente.

> Fuentes: enlaces en la tabla.
