import { SEARCH_CATEGORIES } from '../../lib/constants';

// Shown before any search runs (empty search bar). Explains what the app is and
// the kinds of searches the collection supports, with clickable examples that
// kick off a real search via `onPick`.
export default function SearchLanding({ onPick }) {
  return (
    <section className="mx-auto max-w-3xl space-y-8 py-4 sm:py-8">
      <div className="space-y-3 text-center">
        <h1 className="text-2xl font-bold tracking-tight text-ink sm:text-3xl">
          Explore The Met's open collection
        </h1>
        <p className="mx-auto max-w-xl text-muted">
          Search hundreds of thousands of artworks from The Metropolitan Museum
          of Art — paintings, sculpture, armor, textiles, and more. Type anything
          into the search bar above to begin, then open any piece for a closer look.
        </p>
      </div>

      <div className="space-y-3">
        <p className="text-sm font-medium text-muted">
          Not sure where to start? Try searching by:
        </p>
        <ul className="divide-y divide-line rounded-xl border border-line bg-surface">
          {SEARCH_CATEGORIES.map((category) => (
            <li
              key={category.label}
              className="flex flex-col gap-2 p-4 sm:flex-row sm:items-center sm:gap-4"
            >
              <div className="sm:w-44 sm:shrink-0">
                <p className="font-medium text-ink">{category.label}</p>
                <p className="text-sm text-muted">{category.hint}</p>
              </div>
              <div className="flex flex-wrap gap-2">
                {category.examples.map((term) => (
                  <button
                    key={term}
                    type="button"
                    onClick={() => onPick(term)}
                    className="rounded-full border border-line px-3 py-1 text-sm text-ink transition-colors hover:bg-surface-2 hover:text-accent"
                  >
                    {term}
                  </button>
                ))}
              </div>
            </li>
          ))}
        </ul>
      </div>
    </section>
  );
}
