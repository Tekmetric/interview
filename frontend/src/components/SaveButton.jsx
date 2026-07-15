import { useCollection } from '../context/CollectionContext';
import { IconButton } from './Button';
import { IconBookmark } from './icons';

export default function SaveButton({ artwork, className = '' }) {
  const { isSaved, toggle } = useCollection();
  const saved = isSaved(artwork.id);
  const label = saved ? 'Remove from collection' : 'Save to collection';

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
