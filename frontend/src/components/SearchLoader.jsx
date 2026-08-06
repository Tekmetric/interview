const BAR_COUNT = 8;
const DURATION = 1.6; // seconds; must match the `art-sift` keyframe in index.css

export default function SearchLoader() {
  return (
    <div
      role="status"
      aria-live="polite"
      className="flex flex-col items-center gap-6 py-24"
    >
      <div className="flex h-16 items-center gap-3" aria-hidden="true">
        {Array.from({ length: BAR_COUNT }).map((_, i) => (
          <span
            key={i}
            className="art-sift-bar h-16 w-1.5 rounded-full bg-line"
            style={{ animationDelay: `${((i * DURATION) / BAR_COUNT).toFixed(2)}s` }}
          />
        ))}
      </div>
      <p className="text-sm text-muted">Discovering art…</p>
    </div>
  );
}
