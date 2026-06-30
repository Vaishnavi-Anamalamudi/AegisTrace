# Brand Guidelines

## Identity

| Element | Value |
| --- | --- |
| Product | AegisTrace |
| Tagline | Enterprise Cybersecurity Threat Detection & Incident Response Platform |
| Voice | Precise, defensive, operational, trustworthy |

## Palette

| Token | Hex | Usage |
| --- | --- | --- |
| Void | `#071311` | Primary background |
| Panel | `#0B1F1A` | Cards, panels, dark surfaces |
| Border | `#123D31` | Dividers and grid lines |
| Aegis Green | `#00E676` | Primary signal, logo stroke, success |
| Trace Teal | `#1DE9B6` | Secondary highlight |
| Text | `#E8FFF3` | Primary text on dark |
| Muted Text | `#7BE6A7` | Supporting text |
| Critical | `#EF4444` | Critical security state |
| Warning | `#F59E0B` | Elevated risk state |

## Typography

- Display and UI: Inter, Segoe UI, Arial, sans-serif
- Technical labels: JetBrains Mono, Consolas, monospace

## Assets

- Logo: [../assets/brand/logo.svg](../assets/brand/logo.svg)
- Icon: [../assets/brand/cybersecurity-icon.svg](../assets/brand/cybersecurity-icon.svg)
- Repository banner: [../assets/brand/repository-banner.svg](../assets/brand/repository-banner.svg)
- Open Graph banner: [../assets/brand/open-graph-banner.svg](../assets/brand/open-graph-banner.svg)
- Social preview: [../assets/brand/social-preview.svg](../assets/brand/social-preview.svg)
- Favicon: [../assets/brand/favicon.svg](../assets/brand/favicon.svg)
- Application logo: [../assets/brand/application-logo.svg](../assets/brand/application-logo.svg)

## Usage Rules

- Preserve contrast on dark backgrounds.
- Do not stretch logo artwork.
- Do not place green text on green surfaces.
- Use red and amber only for security states.
- Keep screenshots redacted and operationally realistic.

## Interface States

| State | Visual Treatment |
| --- | --- |
| Loading | Primary action with compact spinner and unchanged layout dimensions |
| Empty | Dashed green panel, small Lucide icon, direct explanation of the missing data source |
| Error | Branded dark page with sanitized operational language and console recovery action |
| Focus | High-contrast green outline with visible offset for keyboard users |
| Success | Short toast in the lower-right notification stack |

## Application Routes

| Route | Brand Role |
| --- | --- |
| `/` | Public product overview |
| `/dashboard` | SOC command console |
| `/incidents` | Incident reconstruction workspace |
| `/evidence` | Evidence vault |
| `/threat-intel` | Exposure findings and scan history |
| `/error/404.html` | Branded route-not-found experience |
