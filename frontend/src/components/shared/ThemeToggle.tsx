import { ComputerDesktopIcon as MonitorIcon, MoonIcon, SunIcon } from '@heroicons/react/24/outline';
import React from 'react';

import { useTheme } from '../../contexts/ThemeContext';
import { Theme } from '../../types';

/**
 * Modern theme toggle component with three adjacent buttons for system, light, and dark modes
 * Follows segmented control design pattern similar to modern UI frameworks
 */
export const ThemeToggle: React.FC = () => {
  const { theme, setTheme } = useTheme();

  const themeOptions = [
    { value: 'system' as Theme, label: 'System', icon: 'monitor' },
    { value: 'light' as Theme, label: 'Light', icon: 'sun' },
    { value: 'dark' as Theme, label: 'Dark', icon: 'moon' },
  ];

  const renderIcon = (iconType: string, isActive: boolean) => {
    const iconClasses = `h-4 w-4 transition-all duration-200 ${isActive ? 'opacity-100' : 'opacity-70'}`;

    switch (iconType) {
      case 'monitor':
        return <MonitorIcon className={iconClasses} />;
      case 'sun':
        return <SunIcon className={iconClasses} />;
      case 'moon':
        return <MoonIcon className={iconClasses} />;
      default:
        return null;
    }
  };

  return (
    <div
      className='inline-flex rounded-lg border border-gray-300 bg-white p-1 transition-colors duration-200 dark:border-gray-600 dark:bg-gray-800'
      role='group'
      aria-label='Theme selection'
    >
      {themeOptions.map((option, index) => {
        const isActive = theme === option.value;
        const isFirst = index === 0;
        const isLast = index === themeOptions.length - 1;

        return (
          <button
            key={option.value}
            onClick={() => setTheme(option.value)}
            className={`
              relative inline-flex items-center justify-center px-3 py-1.5 text-xs font-medium transition-all duration-200
              focus:outline-none
              ${isFirst ? 'rounded-l-md' : ''} 
              ${isLast ? 'rounded-r-md' : ''}
              ${
                isActive
                  ? 'bg-blue-100 text-blue-700 shadow-sm dark:bg-blue-900/50 dark:text-blue-300'
                  : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-gray-200'
              }
            `}
            aria-label={`Switch to ${option.label} theme`}
            aria-pressed={isActive}
            title={`Switch to ${option.label} theme`}
          >
            <span className='flex items-center space-x-1'>
              {renderIcon(option.icon, isActive)}
              <span className='hidden sm:inline'>{option.label}</span>
            </span>
          </button>
        );
      })}
    </div>
  );
};
