import { useState } from 'react';
import { useTranslation } from '../i18n/LocaleProvider';
import { useCollection } from '../context/CollectionContext';
import { useArtworkModal } from '../context/ArtworkModalContext';
import { downloadCollection } from '../lib/exportCollection';
import ResultsList from '../features/results/ResultsList';
import StatusMessage from '../components/StatusMessage';
import ConfirmDialog from '../components/ConfirmDialog';
import Button from '../components/Button';
import { IconBookmark, IconDownload, IconTrash } from '../components/icons';

export default function CollectionPage() {
  const { t } = useTranslation();
  const { items, clear } = useCollection();
  const { open } = useArtworkModal();
  const [confirmOpen, setConfirmOpen] = useState(false);

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <h1 className="text-2xl font-bold tracking-tight text-ink">
          {t('collection.title')}
        </h1>
        {items.length > 0 && (
          <div className="flex flex-wrap items-center gap-3">
            <span className="text-sm text-muted">
              {t('collection.count', { count: items.length })}
            </span>
            <Button variant="secondary" size="sm" onClick={() => downloadCollection(items)}>
              <IconDownload className="size-4" />
              {t('collection.download')}
            </Button>
            <Button variant="danger" size="sm" onClick={() => setConfirmOpen(true)}>
              <IconTrash className="size-4" />
              {t('collection.clear')}
            </Button>
          </div>
        )}
      </div>

      {items.length === 0 ? (
        <StatusMessage
          icon={<IconBookmark className="size-6" />}
          title={t('collection.emptyTitle')}
          body={t('collection.emptyBody')}
        />
      ) : (
        <ResultsList items={items} onSelect={open} showImageLink />
      )}

      {confirmOpen && (
        <ConfirmDialog
          title={t('collection.confirmTitle')}
          body={t('collection.confirmBody', { count: items.length })}
          confirmLabel={t('collection.clear')}
          cancelLabel={t('common.cancel')}
          onConfirm={() => {
            clear();
            setConfirmOpen(false);
          }}
          onCancel={() => setConfirmOpen(false)}
        />
      )}
    </div>
  );
}
