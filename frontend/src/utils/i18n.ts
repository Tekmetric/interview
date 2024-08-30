import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import enUS from './locales/en-US.json';
import frFR from './locales/fr-FR.json';

i18n.use(initReactI18next).init({
  resources: {
    en: { ...enUS },
    fr: { ...frFR },
  },
  lng: 'en', // Default language
  interpolation: {
    escapeValue: false,
  },
});

export default i18n;
