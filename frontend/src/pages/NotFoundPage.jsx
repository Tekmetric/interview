import { Link } from 'react-router-dom';
import { useTranslation } from '../i18n/LocaleProvider';
import { IconImage } from '../components/icons';

export default function NotFoundPage() {
  const { t } = useTranslation();
  return (
    <div className="flex flex-col items-center gap-4 py-16 text-center">
      <span className="flex size-14 items-center justify-center rounded-full bg-surface-2 text-muted">
        <IconImage className="size-7" />
      </span>
      <h1 className="text-2xl font-bold">{t('notFound.title')}</h1>
      <p className="text-muted">{t('notFound.body')}</p>
      <Link to="/" className="text-accent hover:underline">
        {t('notFound.back')}
      </Link>
    </div>
  );
}
