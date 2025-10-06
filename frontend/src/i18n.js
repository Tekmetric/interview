import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

// Import translation files
import translationEN from './locales/en.json';
import translationES from './locales/es.json';
import translationJA from './locales/ja.json';
import translationFR from './locales/fr.json';
import translationDE from './locales/de.json';

// the translations
const resources = {
  en: {
    translation: translationEN
  },
  es: {
    translation: translationES
  },
  ja: {
    translation: translationJA
  },
  fr: {
    translation: translationFR
  },
  de: {
    translation: translationDE
  }
};

i18n
  .use(initReactI18next) // passes i18n down to react-i18next
  .init({
    resources,
    lng: 'en', // default language
    fallbackLng: 'en', // fallback language if translation is missing
    interpolation: {
      escapeValue: false // react already safes from xss
    }
  });

export default i18n;
