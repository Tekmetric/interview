import { useTranslation } from '../../i18n/LocaleProvider';
import { IconClose } from '../../components/icons';

export default function SearchBar({ value, onChange, onSubmit, pending = false }) {
  const { t } = useTranslation();
  const showClear = value.length > 0 && !pending;

  return (
    <div className="relative">
      <input
        type="search"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        onKeyDown={(e) => {
          if (e.key === 'Enter') {
            e.preventDefault();
            onSubmit?.();
          }
        }}
        placeholder={t('search.placeholder')}
        aria-label={t('search.label')}
        className="h-11 w-full rounded-lg border border-line bg-surface px-4 pr-11 text-ink placeholder:text-muted"
      />

      {pending && (
        <span className="absolute inset-y-0 right-3 flex items-center" aria-hidden="true">
          <span className="size-4 animate-spin rounded-full border-2 border-line border-t-accent" />
        </span>
      )}

      {showClear && (
        <button
          type="button"
          onClick={() => onChange('')}
          aria-label={t('search.clear')}
          className="absolute inset-y-0 right-2 flex items-center px-1 text-muted transition-colors hover:text-ink"
        >
          <IconClose className="size-4" />
        </button>
      )}
    </div>
  );
}
