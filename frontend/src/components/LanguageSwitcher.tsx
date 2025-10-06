import { useTranslation } from 'react-i18next';

interface Language {
  code: string;
  name: string;
  flag: string;
}

const LanguageSwitcher: React.FC = () => {
  const { i18n, t } = useTranslation();

  const languages: Language[] = [
    { code: 'en', name: t('language.en'), flag: '🇺🇸' },
    { code: 'es', name: t('language.es'), flag: '🇪🇸' },
    { code: 'ja', name: t('language.ja'), flag: '🇯🇵' },
    { code: 'fr', name: t('language.fr'), flag: '🇫🇷' },
    { code: 'de', name: t('language.de'), flag: '🇩🇪' }
  ];

  const changeLanguage = (lng: string): void => {
    i18n.changeLanguage(lng);
  };

  return (
    <div className="flex items-center gap-2">
      <select
        id="language-select"
        value={i18n.language}
        onChange={(e) => changeLanguage(e.target.value)}
        className="px-3 py-1.5 h-9 text-sm rounded-lg border-none bg-white bg-opacity-20 text-white font-medium cursor-pointer hover:bg-opacity-30 transition-all focus:outline-none focus:ring-2 focus:ring-white focus:ring-opacity-50"
        aria-label={t('language.select')}
      >
        {languages.map(({ code, name, flag }) => (
          <option key={code} value={code} className="bg-gray-800 text-white">
            {flag} {name}
          </option>
        ))}
      </select>
    </div>
  );
};

export default LanguageSwitcher;
