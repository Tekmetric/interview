import {
  HomeIcon,
  QuestionMarkCircleIcon as HelpIcon,
  UsersIcon,
} from '@heroicons/react/24/outline';
import React from 'react';
import { NavLink } from 'react-router-dom';

interface NavItem {
  path: string;
  label: string;
  icon: React.ComponentType<{ className?: string }>;
  badge?: string;
  description?: string;
}

const navItems: NavItem[] = [
  {
    path: '/',
    label: 'Home',
    icon: HomeIcon,
    description: 'Dashboard overview',
  },
  {
    path: '/users',
    label: 'Users',
    icon: UsersIcon,
    description: 'User management',
  },
  {
    path: '/help',
    label: 'Help',
    icon: HelpIcon,
    description: 'Documentation and guides',
  },
];

export const Navigation: React.FC = () => {
  return (
    <nav
      className='border-b border-gray-200 bg-white shadow-sm dark:border-gray-700 dark:bg-gray-800'
      role='navigation'
      aria-label='Main navigation'
    >
      <div className='container mx-auto px-4'>
        <div className='flex h-16 items-center justify-between'>
          {/* Navigation Links */}
          <div className='flex items-center space-x-1' role='menubar'>
            {navItems.map(item => (
              <NavLink
                key={item.path}
                to={item.path}
                className={({ isActive }) =>
                  `px-3 py-2 rounded-md text-sm font-medium transition-colors duration-200 group relative
                  ${
                    isActive
                      ? 'bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-300'
                      : 'text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white hover:bg-gray-100 dark:hover:bg-gray-700'
                  }`
                }
                title={item.description}
                role='menuitem'
                aria-label={`${item.label} - ${item.description}`}
              >
                <span className='flex items-center space-x-2'>
                  <item.icon className='size-4' aria-hidden='true' />
                  <span>{item.label}</span>
                  {item.badge && (
                    <span
                      className='rounded-full bg-blue-500 px-2 py-0.5 text-xs font-semibold text-white'
                      aria-label={`${item.badge} notifications`}
                    >
                      {item.badge}
                    </span>
                  )}
                </span>
              </NavLink>
            ))}
          </div>
        </div>
      </div>
    </nav>
  );
};
