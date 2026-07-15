import { useTranslation } from '../i18n/LocaleProvider';

export default function Spinner({ labelKey = 'spinner.loading' }) {
  const { t } = useTranslation();
  return (
    <div
      role="status"
      aria-live="polite"
      className="flex items-center justify-center gap-3 py-12 text-muted"
    >
      <span
        aria-hidden="true"
        className="size-5 animate-spin rounded-full border-2 border-line border-t-accent"
      />
      <span>{t(labelKey)}…</span>
    </div>
  );
}
