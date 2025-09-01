import React from 'react';

import { ThemeToggle } from './index';

export const Header: React.FC = React.memo(() => {
  return (
    <header
      className='bg-white shadow transition-colors duration-200 dark:bg-gray-800 dark:shadow-gray-700/20'
      role='banner'
    >
      <div className='container mx-auto px-4'>
        <div className='flex items-center justify-between py-6'>
          <div>
            <h1 className='text-3xl font-bold text-gray-900 dark:text-white transition-colors duration-200'>
              User Management Dashboard
            </h1>
            <p className='mt-1 text-lg text-gray-600 dark:text-gray-300 transition-colors duration-200'>
              Manage your users with CRUD operations, search, sort, and pagination
            </p>
          </div>
          <ThemeToggle />
        </div>
      </div>
    </header>
  );
});

Header.displayName = 'Header';
