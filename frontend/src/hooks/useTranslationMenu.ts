import { useTranslation } from 'react-i18next';
import { useState, MouseEvent } from 'react';
import LanguageTypes from '@/types/Language.ts';

const useTranslationMenu = () => {
  const { i18n, t: translate } = useTranslation();
  const { language: selectedLanguage } = i18n;
  const [menuEl, setMenuEl] = useState<null | HTMLElement>(null);

  const handleOnClickLanguage = (event: MouseEvent<HTMLElement>) => {
    setMenuEl(event.currentTarget);
  };

  const handleLanguageClose = () => {
    setMenuEl(null);
  };

  const handleLanguageChange = (language: LanguageTypes) => {
    i18n.changeLanguage(language).then(() => handleLanguageClose());
  };

  return {
    translate,
    selectedLanguage,
    menuEl,
    setMenuEl,
    handleLanguageClose,
    handleOnClickLanguage,
    handleLanguageChange,
  };
};

export default useTranslationMenu;
