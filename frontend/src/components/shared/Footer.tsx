import React from 'react';

export const Footer: React.FC = React.memo(() => {
  return (
    <footer
      className='border-t border-gray-200 bg-white transition-colors duration-200 dark:border-gray-700 dark:bg-gray-800'
      role='contentinfo'
    >
      <div className='mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8'>
        <div className='text-center'>
          <p className='text-sm text-gray-600 transition-colors duration-200 dark:text-gray-300'>
            Built with React 18, TypeScript, and Tailwind CSS
          </p>
          <p className='mt-2 text-xs text-gray-600 transition-colors duration-200 dark:text-gray-300'>
            Features: CRUD operations, search, sorting, pagination, and responsive design
          </p>
        </div>
      </div>
    </footer>
  );
});

Footer.displayName = 'Footer';
