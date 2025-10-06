/**
 * Settings Menu Component
 *
 * Hovering container for dark mode toggle and language switcher
 */

import React, { useState, useRef, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import DarkModeToggle from './DarkModeToggle';
import LanguageSwitcher from './LanguageSwitcher';

const SettingsMenu: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);
  const { t } = useTranslation();

  // Close menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen]);

  return (
    <div ref={menuRef} className="relative">
      {/* Settings Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="flex items-center gap-2 px-3 py-1.5 h-9 text-sm rounded-lg bg-white bg-opacity-20 text-white font-medium hover:bg-opacity-30 transition-all focus:outline-none focus:ring-2 focus:ring-white focus:ring-opacity-50"
        aria-label={t('settings.menu')}
        aria-expanded={isOpen}
        aria-haspopup="true"
      >
        <span className="text-lg" role="img" aria-hidden="true">
          ⚙️
        </span>
        <span className="hidden sm:inline">Settings</span>
      </button>

      {/* Dropdown Menu */}
      {isOpen && (
        <div
          className="absolute right-0 mt-2 w-64 rounded-lg bg-white dark:bg-gray-800 shadow-lg border border-gray-200 dark:border-gray-700 overflow-hidden z-50"
          role="menu"
          aria-orientation="vertical"
        >
          {/* Menu Header */}
          <div className="px-4 py-3 border-b border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-900">
            <h3 className="text-sm font-semibold text-gray-700 dark:text-gray-200">
              Settings
            </h3>
          </div>

          {/* Menu Items */}
          <div className="p-3 space-y-3">
            {/* Dark Mode Section */}
            <div className="flex items-center justify-between">
              <label className="text-sm font-medium text-gray-700 dark:text-gray-200">
                {t('theme.mode')}
              </label>
              <DarkModeToggle />
            </div>

            {/* Divider */}
            <div className="border-t border-gray-200 dark:border-gray-700"></div>

            {/* Language Section */}
            <div>
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-200 mb-2">
                {t('language.label')}
              </label>
              <LanguageSwitcher />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SettingsMenu;
