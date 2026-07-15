import { useState } from 'react';
import { useTranslation } from '../../i18n/LocaleProvider';
import { translateMedium } from '../../lib/mediums';
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
  const { t, locale } = useTranslation();
  const [zoomed, setZoomed] = useState(false);
  const title = artwork.title || t('artwork.untitled');

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
            <IconButton tone="ink" onClick={onClose} aria-label={t('artwork.close')}>
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
                aria-label={t('artwork.fullscreen')}
                title={t('artwork.fullscreen')}
                className="inline-flex items-center justify-center rounded-md bg-black/50 p-2 text-white transition-colors hover:bg-black/70"
              >
                <IconExpand className="size-4" />
              </button>
              <a
                href={artwork.image}
                target="_blank"
                rel="noreferrer"
                aria-label={t('artwork.openImage')}
                title={t('artwork.openImage')}
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
          <Field label={t('artwork.artist')} value={artwork.artist || t('artwork.artistUnknown')} />
          <Field label={t('artwork.date')} value={artwork.date} />
          <Field label={t('artwork.medium')} value={translateMedium(artwork.medium, locale)} />
          <Field label={t('artwork.department')} value={artwork.department} />
          <Field label={t('artwork.culture')} value={artwork.culture} />
        </dl>

        {artwork.url && (
          <a
            href={artwork.url}
            target="_blank"
            rel="noreferrer"
            className="mt-4 inline-flex items-center gap-1.5 text-accent hover:underline"
          >
            {t('artwork.viewOnMet')}
            <IconExternal className="size-4" />
          </a>
        )}
      </ModalShell>

      {/* In-tab lightbox (not the browser Fullscreen API). */}
      {zoomed && artwork.image && (
        <ModalShell
          label={title}
          onClose={() => setZoomed(false)}
          backdropClassName="fixed inset-0 z-[60] flex bg-black/90 p-4"
          className="relative flex h-full w-full items-center justify-center border-0 bg-transparent p-0 shadow-none"
        >
          <img
            src={artwork.image}
            alt={title}
            className="max-h-full max-w-full object-contain"
          />
          {/* `absolute` (not `fixed`) so the focus trap can find it — fixed
              elements report offsetParent === null and get filtered out. */}
          <button
            type="button"
            onClick={() => setZoomed(false)}
            aria-label={t('artwork.close')}
            className="absolute right-2 top-2 inline-flex items-center justify-center rounded-full bg-black/50 p-2 text-white transition-colors hover:bg-black/70"
          >
            <IconClose className="size-6" />
          </button>
        </ModalShell>
      )}
    </>
  );
}
