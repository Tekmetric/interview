import { IconButton } from '../../components/Button';
import PublicDomainMark from '../../components/PublicDomainMark';
import SaveButton from '../../components/SaveButton';
import { IconImage, IconExternal } from '../../components/icons';

// Stagger each row's fade-in, capped so later rows don't wait too long.
function fadeDelay(index) {
  return `${Math.min(0.2 + index * 0.05, 0.4)}s`;
}

export default function ArtworkRow({ artwork, index = 0, onSelect, showImageLink = false }) {
  const title = artwork.title || 'Untitled';
  const artist = artwork.artist || 'Unknown artist';
  const meta = [artwork.medium, artwork.department].filter(Boolean).join(' · ');

  return (
    <li
      className="fade-in-item flex items-stretch rounded-lg border border-line bg-surface transition-colors hover:bg-surface-2"
      style={{ animationDelay: fadeDelay(index) }}
    >
      <button
        type="button"
        onClick={() => onSelect(artwork)}
        className="flex min-w-0 flex-1 items-stretch gap-4 text-left"
      >
        {/* Absolute image so row height follows the text, not the image aspect
            ratio — keeps rows uniform while the thumbnail bleeds edge-to-edge. */}
        <span className="relative w-28 shrink-0 self-stretch overflow-hidden rounded-l-lg bg-surface-2">
          {artwork.thumbnail ? (
            <img
              src={artwork.thumbnail}
              alt={title}
              loading="lazy"
              className="absolute inset-0 size-full object-cover"
            />
          ) : (
            <span className="absolute inset-0 flex items-center justify-center text-muted">
              <IconImage className="size-8" />
            </span>
          )}
        </span>

        <span className="min-w-0 flex-1 py-3 pr-3 sm:pr-0">
          <span className="block truncate font-medium text-ink">{title}</span>
          <span className="block truncate text-sm text-muted">
            {artist}
            {artwork.date ? ` · ${artwork.date}` : ''}
          </span>
          {meta && <span className="block truncate text-xs text-muted">{meta}</span>}
          {/* The visible public-domain mark is hidden on mobile (below), so keep
              the status for screen readers there without taking visible space. */}
          {artwork.isPublicDomain && (
            <span className="sr-only sm:hidden">Public domain</span>
          )}
        </span>
      </button>

      {/* Hidden on mobile so the title/artist/meta get the full row width; these
          actions stay available in the detail view opened by tapping the row. */}
      <div className="hidden shrink-0 items-center gap-2 py-3 pl-1 pr-3 sm:flex">
        {artwork.isPublicDomain && <PublicDomainMark />}
        {showImageLink && artwork.image && (
          <IconButton
            as="a"
            href={artwork.image}
            target="_blank"
            rel="noreferrer"
            aria-label="Open image in new tab"
            title="Open image in new tab"
          >
            <IconExternal className="size-5" />
          </IconButton>
        )}
        <SaveButton artwork={artwork} />
      </div>
    </li>
  );
}
