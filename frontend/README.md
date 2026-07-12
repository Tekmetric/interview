# Rick & Morty Wiki

A wiki-style explorer for the [Rick and Morty API](https://rickandmortyapi.com/documentation): browse, search and filter characters, episodes and locations, follow the cross-links between them, and keep favorites. Built as a client-only SPA — no API key or backend required.

## Quick start

```bash
npm install
npm start        # dev server on http://localhost:5173
```

Requires Node 20+.

| Script                | What it does                                          |
| --------------------- | ----------------------------------------------------- |
| `npm start` / `dev`   | Vite dev server                                       |
| `npm run build`       | Type-check + production build                         |
| `npm run preview`     | Serve the production build                            |
| `npm test`            | Unit/component tests (Vitest + Testing Library + MSW) |
| `npm run test:e2e`    | Playwright e2e + axe accessibility gate               |
| `npm run lint`        | ESLint (flat config)                                  |
| `npm run lint:styles` | Stylelint over the styled-components CSS              |
| `npm run typecheck`   | `tsc` across app + tooling projects                   |
| `npm run format`      | Prettier                                              |

## Tech stack

- **React 19 + TypeScript 5 (strict)** on **Vite** — lightweight setup per the assignment guidelines, no meta-framework.
- **Redux Toolkit 2** — RTK Query for **server state** (caching, request dedup, infinite queries for Load More); classic slices + listener middleware for **client state** (theme, locale, favorites). The server/client state split is deliberate: cached API data and user preferences have different lifecycles.
- **styled-components 6** — typed theme via `DefaultTheme` augmentation, light/dark palettes, global styles.
- **react-intl** — full internationalization, English + Romanian, ICU plurals (Romanian's three-way plural rules actually exercise it).
- **react-router 8** (library mode) — URL-addressable pages, route-level code splitting via `lazy`.
- **@tanstack/react-virtual** — windowed character grid.
- **Vitest + React Testing Library + MSW** and **Playwright + axe-core** for tests.

No axios (RTK Query's `fetchBaseQuery` wraps `fetch`), no component library (all UI is hand-rolled styled-components).

## Features

- **Characters** — searchable (debounced, URL-synced) and filterable (status/gender) grid with Load More pagination; the grid is virtualized so the DOM stays flat as pages accumulate.
- **Character / episode / location detail pages** — cross-linked in every direction (character → episodes and locations, episode → cast, location → residents) via the API's resource URLs, batch-fetched in a single request per section.
- **Episodes** — all 51 episodes fetched once, grouped into an accessible season accordion, filtered client-side instantly.
- **Favorites** — heart any character, episode or location; persisted to `localStorage` through Redux listener middleware; collected on a favorites page.
- **Theme switching** — light/dark with `prefers-color-scheme` default, WCAG AA contrast-checked palettes, `color-scheme` sync for native UI.
- **i18n** — complete English and Romanian catalogs, persisted locale, `<html lang>` kept in sync.
- **Random character portal** — jump to a random character out of all 826.
- Themed loading, error (with retry) and empty states — including the API quirk where a search with no matches returns HTTP 404 rather than an empty list.

## Architecture notes

```
src/
  app/        store, providers, router — application wiring
  api/        api slice, shared response types, pagination helper
  features/   one folder per domain: characters, episodes, locations,
              favorites, theme, locale, random — each owns its endpoints,
              pages and components (endpoints via injectEndpoints)
  components/ shared UI: layout, feedback states, badges, icons
  theme/      design tokens, light/dark palettes, global style
  i18n/       message catalogs
  test/       shared test infra: MSW server, fixtures, API simulator,
              renderWithProviders
```

- **The URL is the source of truth for list state** — search and filters live in query params, so results are shareable, refresh-safe and back-button friendly. Changing a filter changes the RTK Query cache key, which resets pagination for free.
- **Two data-fetching strategies on purpose**: characters (826) use server-driven infinite queries; episodes (51, finite) are fetched wholesale and grouped/searched client-side. Each matches its dataset size.
- **API quirks are normalized at the boundary**: batch endpoints (`/character/1,2,3`) return a bare object for a single id — `transformResponse` normalizes; 404-for-no-matches is mapped to an empty state, not an error screen.

## Accessibility

Targets WCAG 2.1 AA: semantic landmarks and heading structure, skip link, focus moved to `<main>` on route change (SPA navigations are announced), single-link card pattern (one tab stop per card, stretched-link for pointer users), `aria-live` result counts, toggle buttons with `aria-pressed`, WAI-ARIA disclosure pattern for the season accordion, status conveyed by text + color (never color alone), contrast-verified palettes in both themes, `prefers-reduced-motion` respected globally, `eslint-plugin-jsx-a11y` in lint. The e2e suite gates on **zero serious/critical axe violations** across key screens in both themes.

## Performance

- Route-level code splitting (each page is its own chunk; the shell stays small).
- Windowed character grid — with 120+ characters loaded the DOM holds only the rows near the viewport (measured: ~16 cards in the DOM regardless of pages loaded).
- Debounced search (300 ms) — one request per pause, not per keystroke.
- RTK Query caching — revisiting pages and details renders from cache, duplicate requests are deduped.
- Images: `loading="lazy"`, explicit dimensions (no layout shift), `decoding="async"`; the detail portrait is `fetchPriority="high"` (LCP).

## Testing

- **Unit/component** (`*.test.*`): Vitest + Testing Library rendering through the real providers (store, theme, intl, router), with **MSW** answering at the network layer — the RTK Query endpoints, transforms and error mapping are exercised for real.
- **E2e** (`e2e/*.spec.*`): Playwright against the production build. Journeys: browse → search → cross-link navigation, favorites persistence across reloads, theme/locale persistence, virtualization DOM budget, skip-link keyboard flow, and axe scans.
- Both suites share one **API simulator** (`src/test/apiSim.ts`) that faithfully encodes the API contract and its quirks, so unit and e2e mocks can never drift apart. E2e mocks the network for determinism (the live public API rate-limits bursts); the app itself runs against the real API.

## Trade-offs / future work

- Search input → URL sync is one-directional after mount; navigating browser history updates results but not the input text. Accepted for simplicity.
- Episode/location list rows don't have inline favorite hearts (detail pages do). Easy extension.
- `React.memo`/`useMemo` are used sparingly and only where measurement justifies them; the React Compiler was deliberately left out to keep the build explainable.
