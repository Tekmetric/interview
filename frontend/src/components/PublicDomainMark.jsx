import { IconPublicDomain } from './icons';

// Compact icon (with tooltip + aria-label) in lists; icon plus visible label in
// the detail view via `showLabel`.
export default function PublicDomainMark({ showLabel = false }) {
  const label = 'Public domain';

  if (showLabel) {
    return (
      <span className="inline-flex items-center gap-1.5 text-sm text-muted">
        <IconPublicDomain className="size-5" />
        {label}
      </span>
    );
  }

  return (
    <span className="group relative inline-flex" title={label}>
      <span
        role="img"
        aria-label={label}
        className="inline-flex size-8 items-center justify-center rounded-full border border-line text-muted"
      >
        <IconPublicDomain className="size-4" />
      </span>
      <span
        role="tooltip"
        className="pointer-events-none absolute -top-9 left-1/2 z-10 -translate-x-1/2 whitespace-nowrap rounded-md bg-ink px-2 py-1 text-xs font-medium text-canvas opacity-0 shadow transition-opacity group-hover:opacity-100 group-focus-within:opacity-100"
      >
        {label}
      </span>
    </span>
  );
}
