# Met Collection Search — Build Plan

A small, focused single-page React app for the Tekmetric frontend take-home.
Searches **The Metropolitan Museum of Art's open collection** via the free,
keyless [Met Collection API](https://metmuseum.github.io/) and displays the
results as a filterable **list**. Built to be fully explainable line-by-line —
see the in-app **"How I built this"** page.

> This app reuses the base (Vite + React Router + Tailwind + i18n + theming +
> primitives) from an earlier surf-conditions iteration, refocused onto a
> list/search feature to sit closer to the brief's "Table, List, etc."

## Product

- Search the collection by term (subject, artist, culture).
- Results as a **list** of rows: thumbnail, title, artist, date, medium,
  department, and a public-domain indicator.
- Filter by **department**.
- **Load more** to page through matches.
- Click any work for a **detail modal** (larger image, metadata, link to The Met).

## Data source (keyless, CORS-enabled)

| Purpose | Endpoint |
| --- | --- |
| Department list | `.../public/collection/v1/departments` |
| Search → object IDs + total | `.../public/collection/v1/search?q=&hasImages=true[&departmentId=]` |
| Object detail | `.../public/collection/v1/objects/{id}` |

The two-step design (search returns only IDs) drives the architecture: page
through the IDs and fetch each object's detail lazily, in parallel per page.

## Tech stack & rationale

- **Vite + React 18** — lightweight SPA, replacing the old CRA scaffold.
- **React Router 7 (library mode)** — real pages, not a meta-framework.
- **Tailwind v4** — utilities behind small named components.
- **Custom `Intl`-based i18n** — 5 UI languages; artwork data stays in the
  museum's language. `Intl.NumberFormat` for result counts.
- **Cooper Hewitt** font (self-hosted) with a Noto Sans JP fallback.

## Routing

| Route | Page |
| --- | --- |
| `/` | SearchPage — search + results list |
| `/how-i-built-this` | HowIBuiltThisPage (lazy-loaded) |
| `*` | NotFoundPage |

## Architecture — smart page, dumb components

```
src/
  api/metMuseum.js     fetchDepartments, searchObjects, fetchObject, normalizeObject
  hooks/               useArtworkSearch, usePagedArtworks, useDepartments,
                       useDebouncedValue, useLocalStorage
  lib/                 constants (page size), format (count)
  context/             SettingsContext (theme, persisted)
  i18n/                LocaleProvider + t() + catalogs/{en,es,fr,pt,ja}.json
  components/          Badge, Select, Spinner, StatusMessage, Header, …
  features/            search/ (SearchBar, DepartmentFilter),
                       results/ (ResultsList, ArtworkRow), artwork/ (ArtworkModal)
  pages/               SearchPage, HowIBuiltThisPage, NotFoundPage
  layouts/             RootLayout, ErrorBoundary
```

- **SearchPage** is the only smart component: owns query/filter/selection state
  and the data hooks, passes plain props down.
- **Custom hooks** own the async lifecycle and abort stale requests.
- **`normalizeObject`** and formatting are pure and unit-tested (Vitest).

## Accessibility — target WCAG 2.x Level AA

Contrast checked in both themes · public-domain conveyed by text, not color ·
keyboard nav + visible focus · accessible dialog (Escape, focus, click-outside) ·
semantic landmarks + skip link · labeled controls · `aria-live` result count ·
`prefers-reduced-motion` respected.

## Notes & limitations

- Some records lack a usable image despite `hasImages`; handled gracefully.
- Individual object fetches fail independently (dropped, not fatal).
- Search requires a query; the department filter narrows an active search.

## Credits

- Collection data: [The Met Collection API](https://metmuseum.github.io/).
- Typeface: Cooper Hewitt (free for public use).
