# Red: política TLS y endpoints

## network_security_config.xml (completo)

```xml
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">dev-library.jw.org</domain>
    </domain-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

Lectura:
- **Base**: cleartext prohibido (solo HTTPS), trust anchors = **almacén del sistema**.
- **Sin certificate pinning** declarado en el config. (La defensa contra CDN/MITM no
  recae en pinning sino en la **verificación de firma Ed25519 del contenido**, doc 02.)
- **Excepción**: `dev-library.jw.org` (y subdominios) permite **HTTP en claro**.
  Es un dominio de **desarrollo** que quedó en el config de producción →
  observación de higiene (no explotable salvo que la app apunte ahí).

## Endpoints referenciados en el binario

### Producción
| Host | Uso probable |
|---|---|
| `www.jw.org` | Sitio / App Links `/finder` / donate |
| `wol.jw.org` | Watchtower Online Library (contenido) |
| `app.jw-cdn.org` | **CDN de distribución** de publicaciones/media |
| `donate.jw.org` | Donaciones |

### Desarrollo / QA (filtrados en el release)
| Host | Nota |
|---|---|
| `dev-library.jw.org` | Dev (cleartext permitido) |
| `app.jw-cdn-qa.org` | CDN de QA |
| `jwl-draft.whqmeps.org` | Borradores internos WHQ-MEPS (×7 refs) |

### Telemetría
| Host | Servicio |
|---|---|
| `in.appcenter.ms` | Microsoft App Center (crash/analytics) |
| `mobile.events.data.microsoft.com` | Telemetría Microsoft |

> Nota: `ns.adobe.com`, `purl.org`, `www.w3.org`, `iptc.org`, `aiim.org`, etc.
> **no son endpoints**: son namespaces XMP/EXIF de `com.drew.metadata`
> (metadata-extractor) y del parser XML. Descartados.

## Observaciones
1. Dominios dev/QA (`dev-library`, `*-qa`, `whqmeps.org`) presentes en el release:
   filtración menor de infraestructura interna; sin credenciales.
2. La ausencia de pinning es una decisión de diseño razonable **porque** el
   contenido va firmado extremo a extremo (PKI del doc 02).
