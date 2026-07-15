import { useRouteError, Link } from 'react-router-dom';
import { useTranslation } from '../i18n/LocaleProvider';

export default function ErrorBoundary() {
  const { t } = useTranslation();
  const error = useRouteError();
  const detail = error?.statusText || error?.message;

  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-4 bg-canvas px-4 text-center text-ink">
      <h1 className="text-2xl font-bold">{t('error.title')}</h1>
      {detail && <p className="text-muted">{detail}</p>}
      <Link to="/" className="text-accent hover:underline">
        {t('error.back')}
      </Link>
    </div>
  );
}
