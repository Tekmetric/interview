import { useState } from 'react';
import ModalShell from '../../components/ModalShell';
import { IconButton } from '../../components/Button';
import PublicDomainMark from '../../components/PublicDomainMark';
import SaveButton from '../../components/SaveButton';
import { IconClose, IconExternal, IconExpand, IconImage } from '../../components/icons';

function Field({ label, value }) {
  if (!value) return null;
  return (
    <div>
      <dt className="text-xs font-medium uppercase tracking-wide text-muted">{label}</dt>
      <dd className="text-ink">{value}</dd>
    </div>
  );
}

export default function ArtworkModal({ artwork, onClose }) {
  const [zoomed, setZoomed] = useState(false);
  const title = artwork.title || 'Untitled';

  return (
    <>
      <ModalShell label={title} onClose={onClose}>
        <div className="mb-4 flex items-start justify-between gap-4">
          <div className="space-y-2">
            <h2 className="text-xl font-bold text-ink">{title}</h2>
            {artwork.isPublicDomain && <PublicDomainMark showLabel />}
          </div>
          <div className="flex shrink-0 items-center gap-2">
            <SaveButton artwork={artwork} />
            <IconButton tone="ink" onClick={onClose} aria-label="Close">
              <IconClose className="size-5" />
            </IconButton>
          </div>
        </div>

        {artwork.image ? (
          <div className="relative mb-4">
            <img
              src={artwork.image}
              alt={title}
              onClick={() => setZoomed(true)}
              className="max-h-[60vh] w-full cursor-zoom-in rounded-lg bg-surface-2 object-contain"
            />
            <div className="absolute right-2 top-2 flex gap-1.5">
              <button
                type="button"
                onClick={() => setZoomed(true)}
                aria-label="View fullscreen"
                title="View fullscreen"
                className="inline-flex items-center justify-center rounded-md bg-black/50 p-2 text-white transition-colors hover:bg-black/70"
              >
                <IconExpand className="size-4" />
              </button>
              <a
                href={artwork.image}
                target="_blank"
                rel="noreferrer"
                aria-label="Open image in new tab"
                title="Open image in new tab"
                className="inline-flex items-center justify-center rounded-md bg-black/50 p-2 text-white transition-colors hover:bg-black/70"
              >
                <IconExternal className="size-4" />
              </a>
            </div>
          </div>
        ) : (
          <div className="mb-4 flex h-48 w-full items-center justify-center rounded-lg bg-surface-2 text-muted">
            <IconImage className="size-10" />
          </div>
        )}

        <dl className="grid grid-cols-2 gap-3">
          <Field label="Artist" value={artwork.artist || 'Unknown artist'} />
          <Field label="Date" value={artwork.date} />
          <Field label="Medium" value={artwork.medium} />
          <Field label="Department" value={artwork.department} />
          <Field label="Culture" value={artwork.culture} />
        </dl>

        {artwork.url && (
          <a
            href={artwork.url}
            target="_blank"
            rel="noreferrer"
            className="mt-4 inline-flex items-center gap-1.5 text-accent hover:underline"
          >
            View on metmuseum.org
            <IconExternal className="size-4" />
          </a>
        )}
      </ModalShell>

      {/* In-tab lightbox (not the browser Fullscreen API). */}
      {zoomed && artwork.image && (
        <ModalShell
          label={title}
          onClose={() => setZoomed(false)}
          dismissOnClick
          backdropClassName="fixed inset-0 z-[60] flex bg-black/90 p-4"
          className="relative flex h-full w-full items-center justify-center border-0 bg-transparent p-0 shadow-none"
        >
          <img
            src={artwork.image}
            alt={title}
            className="max-h-full max-w-full cursor-zoom-out object-contain"
          />
          {/* `absolute` (not `fixed`) so the focus trap can find it — fixed
              elements report offsetParent === null and get filtered out. */}
          <button
            type="button"
            onClick={() => setZoomed(false)}
            aria-label="Close"
            className="absolute right-2 top-2 inline-flex cursor-pointer items-center justify-center rounded-full bg-black/50 p-2 text-white transition-colors hover:bg-black/70"
          >
            <IconClose className="size-6" />
          </button>
        </ModalShell>
      )}
    </>
  );
}
