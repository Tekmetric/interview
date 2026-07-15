# Met Collection Search

A single-page React app for searching **The Metropolitan Museum of Art's open
collection**, built for the Tekmetric frontend take-home. Search a term, browse
the matching works as a list, filter by department, and open any piece for
detail. It uses the free, keyless, CORS-enabled
[Met Collection API](https://metmuseum.github.io/) — so the whole thing runs
client-side with no backend.

A build plan lives in [PLAN.md](./PLAN.md), and the original assignment brief is
preserved in [ASSIGNMENT.md](./ASSIGNMENT.md).

## Features

- **Landing state** — before you search, a short intro plus example searches
  grouped by category (artist, region, theme, style, medium) that run a real
  search when clicked; nothing is fetched until you search.
- **Full-text search** of the collection (debounced, abortable).
- **Results list** — rows with thumbnail, title, artist, date, medium, department,
  and a public-domain indicator.
- **Department filter** loaded from the API, with a one-click **clear** button.
- **Your Collection** — save/unsave any work; persists to `localStorage` and has
  its own page (with a live count in the nav).
- **Load-more pagination** over the matched works.
- **Detail modal** — larger image + metadata + a link to metmuseum.org
  (accessible dialog: Escape, focus management, click-outside).
- **Light / dark theme**, persisted to `localStorage`.
- Minimal inline **SVG iconography** (no icon fonts, no emoji).
- **Accessibility-minded** throughout (see below).

## Quick start

```bash
npm install
npm run dev      # start the dev server (http://localhost:5173)
npm run build    # production build
npm run preview  # preview the production build
npm test         # run unit tests (Vitest)
```

Requires a recent Node (18+). No API keys or environment variables needed.

## Tech stack

- **Vite** + **React 18** (SPA, replacing the repo's old Create React App scaffold)
- **React Router 7** in library mode (client-only SPA — not framework mode)
- **Tailwind CSS v4** via `@tailwindcss/vite`
- **Vitest** for unit tests
- **Cooper Hewitt** self-hosted typeface

## Architecture

Smart page, dumb components. The Search page owns data fetching and state and
passes plain props down; everything else is presentational.

```
src/
  api/metMuseum.js     departments + search + object fetchers (abortable)
  hooks/               useArtworkBrowse, useArtworkSearch, usePagedArtworks, …
  lib/                 pure helpers (object normalizer, CSV export, status enum)
  context/             SettingsContext (theme), CollectionContext, ArtworkModal
  components/          reusable UI primitives (Button, Select, ModalShell, …)
  features/            search/, results/, artwork/
  pages/               SearchPage, CollectionPage, NotFoundPage
  layouts/             RootLayout, ErrorBoundary
```

- **Two-step data flow**: `/search` returns matching object **IDs** (often
  hundreds); we page through them and fetch each `/objects/{id}` lazily, a page
  at a time, in parallel.
- **Resilient fetching**: individual object failures are dropped rather than
  failing the whole page; rows without an image fall back to a placeholder.
- **Reused detail data**: the object response already includes the full image and
  metadata, so the detail modal needs no extra request.

## Accessibility

Color contrast checked in both themes; information never conveyed by color alone;
full keyboard navigation with visible focus rings; an accessible dialog for the
detail modal; semantic landmarks and a skip link; labeled controls; a live region
for the result count; and `prefers-reduced-motion` respected.

## Notes & limitations

- Result ordering comes from the Met API's own relevance ranking, which is loose
  for broad subject terms (title/artist queries rank best).
- Some records lack a usable image even with the `hasImages` filter; these are
  handled gracefully (placeholder / metadata-only detail).
- Search requires a typed query (nothing is fetched on the landing screen); the
  department filter narrows an active search rather than searching on its own.

## Credits

- Collection data: [The Met Collection API](https://metmuseum.github.io/)
- Typeface: Cooper Hewitt (free for public use)
