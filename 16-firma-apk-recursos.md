# Firma del APK, búsqueda y recursos

## Firma del APK — VERIFICADA

Verificación con `apksigner verify --print-certs`:

| Esquema | Resultado |
|---|---|
| v1 (JAR signing) | no verifica por v1 |
| **v2 (APK Signature Scheme v2)** | **✅ válido** |
| **v3 (APK Signature Scheme v3)** | **✅ válido** |
| v3.1 / v4 | no |

- **Firmantes: 1**
- **Certificado DN:** `CN=Christopher Nelson, OU=MEPS, O=Jehovah's Witnesses,
  L=Patterson, ST=NY, C=US` (Patterson, NY = sede de Watch Tower).
- Clave: **RSA 2048**.
- Cert SHA-256: `7ea74c8e1e69f235d60a23f2917a4ec19a0d1cbc73ebe4670c2ce1bae1014e5d`
- Cert SHA-1: `4bc93d518fc54cd3d73e74a4eb376a4f8f0db3a7`
- Public key SHA-256: `59400e52d3493c28a0953ab175de83cc3c7d67898db0ef3229255f203fe30f18`

**Conclusión:** el APK está firmado legítimamente por Watch Tower (v2+v3). El
certificado es verificable contra el que usa Google Play para esta app.

## Búsqueda

- UI: `SearchPage` + `SearchPageViewModel` (+ `searchable.xml`, doc 01).
- `org.jw.service.metrics.Rank` (campo `float a`): **puntuación de relevancia** para
  ordenar resultados — no es telemetría (doc 08).
- El índice de búsqueda se construye sobre el contenido MEPS indexado (SQLite).

## Recursos (4,144 entradas)

| Tipo | Conteo |
|---|---|
| Layouts XML | 298 |
| Drawables (todas densidades) | 3,062 |
| `strings.xml` localizados | **147 idiomas** |
| Strings (tabla por defecto) | 1,177 |
| Densidades drawable | ldpi…xxxhdpi + night + watch + ldrtl (RTL) |

Variantes de layout: `layout-land`, `layout-sw600dp` (tablet), `layout-h540dp` →
soporte de tablet y orientación. `color-night`, `drawable-night-*` → **modo oscuro**.
`drawable-ldrtl-*` → **idiomas RTL** (árabe, hebreo, urdu — coherente con fuentes
Noto Nastaliq/Naskh de la webapp).

## Assets destacados (recap)
`catalog.db` (13 MB, 4,625 pubs), `mepsunit.jwdat` (9.9 MB), `assets/webapp/` (React),
`assets/javet/icu/icudtl.dat` (ICU), `v2..v16_upgrade.sql` (migraciones userData),
`licenses.html`.
