import { useTranslation } from '../i18n/LocaleProvider';

// Body copy is intentionally English (reviewer-facing); only the title/nav are
// translated, since the artwork data itself isn't localized either.

function Section({ title, children }) {
  return (
    <section className="space-y-3">
      <h2 className="text-xl font-bold text-ink">{title}</h2>
      {children}
    </section>
  );
}

function Decision({ choice, why }) {
  return (
    <li className="space-y-1">
      <p className="font-medium text-ink">{choice}</p>
      <p className="text-sm text-muted">{why}</p>
    </li>
  );
}

export default function HowIBuiltThisPage() {
  const { t } = useTranslation();

  return (
    <article className="mx-auto max-w-2xl space-y-10 leading-relaxed">
      <header className="space-y-2">
        <h1 className="text-3xl font-bold tracking-tight text-ink">
          {t('nav.howBuilt')}
        </h1>
        <p className="text-muted">
          A transparent look at the tooling and decisions behind this app — what
          I used, why, and the trade-offs I weighed. Every line here is code I can
          walk through and defend.
        </p>
      </header>

      <Section title="What it is">
        <p className="text-muted">
          A single-page app for searching{' '}
          <a href="https://metmuseum.github.io/" className="text-accent hover:underline" target="_blank" rel="noreferrer">
            The Met's open Collection API
          </a>
          . You search a term, browse the matching works as a list, filter by
          department, open any piece for detail, and save favorites to your own
          collection (persisted in localStorage). It's keyless and CORS-enabled,
          so the whole thing runs client-side with no backend.
        </p>
      </Section>

      <Section title="Tech stack">
        <ul className="space-y-3">
          <Decision
            choice="Vite + React 18"
            why="A lightweight SPA scaffold, replacing the repo's old Create React App setup (React 16, ~2019). Vite's dev server and build are far faster."
          />
          <Decision
            choice="React Router 7 — library mode"
            why="Real, separate pages (Search, How I built this, and a 404) without adopting a meta-framework. React Router 7 can run as a Remix-style framework; I deliberately used its client-only library mode, per the brief."
          />
          <Decision
            choice="Tailwind CSS v4"
            why="Fast, consistent styling via its first-party Vite plugin. Utilities are encapsulated behind small named components so the markup stays readable."
          />
          <Decision
            choice="Custom Intl-based i18n"
            why="A small t() helper over JSON catalogs plus native Intl for locale-aware number formatting (e.g. result counts). No dependency; the UI ships in 5 languages while the artwork data stays in the museum's language."
          />
          <Decision
            choice="Cooper Hewitt typeface"
            why="Self-hosted, with a Noto Sans JP fallback for Japanese. A warm, elegant palette with light/dark themes."
          />
        </ul>
      </Section>

      <Section title="Architecture">
        <p className="text-muted">
          The guiding principle is <strong className="text-ink">smart page, dumb components</strong>.
          The Search page owns the query/filter/selection state and the data
          hooks; every feature and UI component below it is presentational and
          receives plain props, which keeps them easy to test and reason about.
        </p>
        <ul className="ml-5 list-disc space-y-2 text-muted">
          <li>
            <strong className="text-ink">Two-step data flow:</strong> the search
            endpoint returns only matching object <em>IDs</em> (often hundreds), so
            I page through them and fetch each object's detail lazily — the current
            page of IDs is fetched in parallel with Promise.all.
          </li>
          <li>
            <strong className="text-ink">Custom hooks</strong> own the async
            lifecycle: useDepartments, useArtworkSearch, and usePagedArtworks each
            handle their own loading/error state and abort stale requests with
            AbortController.
          </li>
          <li>
            <strong className="text-ink">Reused detail data:</strong> the object
            endpoint already returns the full-size image and metadata, so the
            detail modal reuses the row's data with no extra request.
          </li>
          <li>
            <strong className="text-ink">Pure helpers</strong> (the object
            normalizer, the CSV export builder) are isolated and side-effect-free,
            so they're trivial to unit-test.
          </li>
        </ul>
      </Section>

      <Section title="Decisions & trade-offs">
        <ul className="space-y-3">
          <Decision
            choice="Hand-rolled data fetching (no data library)"
            why="Search, pagination, and caching are handled with small custom hooks rather than a library like TanStack Query — for simplicity and to keep the data flow explicit and easy to walk through. The trade-off is that request de-duplication, caching, and retries are lighter and done by hand; a larger app would likely reach for a data-fetching library."
          />
          <Decision
            choice="Load-more pagination"
            why="The search returns a full list of IDs; I fetch and append a page at a time on demand rather than all at once, keeping the initial burst of per-object requests bounded."
          />
          <Decision
            choice="List over grid"
            why="A scannable list of rows (thumbnail + title, artist, date, medium) fits the brief's 'table/list' framing and keeps a lot of metadata visible at a glance."
          />
          <Decision
            choice="Resilient per-item fetching"
            why="Some records fail or lack images despite the hasImages filter, so each object fetch fails independently (dropped, not fatal) and rows fall back to a placeholder."
          />
          <Decision
            choice="Debounced search"
            why="Typing is debounced and in-flight searches are aborted, so a slow earlier response can't overwrite a newer query."
          />
        </ul>
      </Section>

      <Section title="Accessibility">
        <p className="text-muted">
          Built toward WCAG 2.x Level AA: color contrast checked in both themes,
          information never conveyed by color alone (the public-domain badge is
          labeled text), full keyboard navigation with visible focus rings, a
          semantic dialog for the detail modal (Escape to close, focus managed),
          semantic landmarks and a skip link, labeled controls, a live region for
          the result count, and prefers-reduced-motion respected.
        </p>
      </Section>

      <Section title="Translations & their limits">
        <p className="text-muted">
          The interface is available in five languages (English, Español,
          Français, Português, 日本語) through a small custom i18n layer, with
          locale-aware number and date formatting via the native{' '}
          <code className="rounded bg-surface-2 px-1 text-sm">Intl</code> API.
        </p>
        <p className="text-muted">
          The important caveat: translation only covers the app's <em>own</em>{' '}
          text. The artwork data comes from the Met API in its source language
          (largely English) — titles, artist names, dates, and departments are
          shown as the museum records them and are <strong className="text-ink">not
          translated</strong>. So switching language localizes the chrome around
          the collection, not the collection itself. That's an inherent limit of
          building on someone else's data.
        </p>
        <p className="text-muted">
          One targeted exception: the <em>medium</em> field. It's free text and
          enormously varied, but a handful of values (e.g. "Oil on canvas",
          "Bronze", "Porcelain") recur constantly, so I map the most common ones
          to translations and fall back to the original English for the long
          tail. It's a deliberately small, high-value slice rather than an
          attempt to translate unbounded museum text.
        </p>
      </Section>

      <Section title="Built with AI — openly">
        <p className="text-muted">
          I used Claude Code as a pair-programmer to scaffold and accelerate this
          build. The product decisions, architecture, and trade-offs were mine,
          and I reviewed every line — I can explain any part of it in detail.
          Being transparent about tooling felt truer to the spirit of the exercise
          than hiding it.
        </p>
      </Section>

      <Section title="Known limitations">
        <ul className="ml-5 list-disc space-y-2 text-muted">
          <li>
            Result ordering comes from the Met API's own relevance ranking, which
            is loose for broad subject terms — title and artist queries rank best.
          </li>
          <li>
            Some records lack a usable image despite the hasImages filter; these
            fall back to a placeholder.
          </li>
        </ul>
      </Section>

      <Section title="Credits">
        <ul className="ml-5 list-disc space-y-1 text-muted">
          <li>Collection data: The Metropolitan Museum of Art Collection API.</li>
          <li>Typeface: Cooper Hewitt (free for public use).</li>
        </ul>
      </Section>
    </article>
  );
}
