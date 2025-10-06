import React from 'react';
import { useTranslation } from 'react-i18next';
import { useAppDispatch, useAppSelector } from '../../../store/hooks';
import { toggleTheme as toggleThemeAction } from '../../../store/themeSlice';

const DarkModeToggle: React.FC = () => {
  const dispatch = useAppDispatch();
  const isDark = useAppSelector((state) => state.theme.isDarkMode);
  const { t } = useTranslation();

  const handleToggle = () => {
    dispatch(toggleThemeAction());
  };

  return (
    <button
      onClick={handleToggle}
      className="flex items-center gap-2 px-3 py-1.5 h-9 text-sm rounded-lg bg-white bg-opacity-20 text-white font-medium hover:bg-opacity-30 transition-all focus:outline-none focus:ring-2 focus:ring-white focus:ring-opacity-50"
      aria-label={isDark ? t('theme.light') : t('theme.dark')}
      title={isDark ? t('theme.switchToLight') : t('theme.switchToDark')}
    >
      <span className="text-lg" role="img" aria-hidden="true">
        {isDark ? '☀️' : '🌙'}
      </span>
    </button>
  );
};

export default DarkModeToggle;
