import { useTranslation } from '../i18n/LocaleProvider';
import Select from './Select';

export default function LanguageSelect() {
  const { locale, setLocale, locales, t } = useTranslation();
  return (
    <Select
      id="language-select"
      label={t('lang.label')}
      value={locale}
      onChange={setLocale}
      options={locales.map((l) => ({ value: l.code, label: l.label }))}
    />
  );
}
