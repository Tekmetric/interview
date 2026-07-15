export default function SearchSuggestions({ label, items, onPick }) {
  return (
    <div className="absolute z-10 mt-1 w-full rounded-lg border border-line bg-surface p-3 shadow-lg">
      <p className="mb-2 text-xs font-medium uppercase tracking-wide text-muted">
        {label}
      </p>
      <div className="flex flex-wrap gap-2">
        {items.map((term) => (
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
    </div>
  );
}
