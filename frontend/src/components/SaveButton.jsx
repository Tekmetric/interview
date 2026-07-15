import { useCollection } from '../context/CollectionContext';
import { useTranslation } from '../i18n/LocaleProvider';
import { IconButton } from './Button';
import { IconBookmark } from './icons';

export default function SaveButton({ artwork, className = '' }) {
  const { isSaved, toggle } = useCollection();
  const { t } = useTranslation();
  const saved = isSaved(artwork.id);
  const label = saved ? t('artwork.remove') : t('artwork.save');

  return (
    <IconButton
      tone={saved ? 'accent' : 'muted'}
      onClick={() => toggle(artwork)}
      aria-pressed={saved}
      aria-label={label}
      title={label}
      className={className}
    >
      <IconBookmark filled={saved} className="size-5" />
    </IconButton>
  );
}
