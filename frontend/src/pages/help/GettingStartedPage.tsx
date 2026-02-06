import React from 'react';

export const GettingStartedPage: React.FC = () => {
  return (
    <div className='space-y-6'>
      <h2 className='text-2xl font-bold text-gray-900 dark:text-white'>Getting Started</h2>

      <div className='prose dark:prose-invert max-w-none'>
        <p className='text-lg text-gray-600 dark:text-gray-300'>
          Welcome to the User Management Dashboard! Follow these simple steps to get started.
        </p>

        <h3 className='mb-4 mt-8 text-xl font-semibold text-gray-900 dark:text-white'>
          Dashboard Overview
        </h3>
        <p className='text-gray-600 dark:text-gray-300'>The dashboard has three main sections:</p>
        <ul className='list-inside list-disc space-y-2 text-gray-600 dark:text-gray-300'>
          <li>
            <strong>Home:</strong> Overview and quick access to features
          </li>
          <li>
            <strong>Users:</strong> Manage users with search, add, edit, and delete functionality
          </li>
          <li>
            <strong>Help:</strong> Documentation and guides
          </li>
        </ul>

        <h3 className='mb-4 mt-8 text-xl font-semibold text-gray-900 dark:text-white'>
          Quick Start Steps
        </h3>
        <div className='space-y-4'>
          <div className='flex items-start space-x-3'>
            <div className='flex size-8 shrink-0 items-center justify-center rounded-full bg-blue-600 text-sm font-semibold text-white'>
              1
            </div>
            <div>
              <h4 className='font-semibold text-gray-900 dark:text-white'>Visit the Users Page</h4>
              <p className='text-gray-600 dark:text-gray-300'>
                Navigate to the Users section to see the user list and available actions.
              </p>
            </div>
          </div>

          <div className='flex items-start space-x-3'>
            <div className='flex size-8 shrink-0 items-center justify-center rounded-full bg-blue-600 text-sm font-semibold text-white'>
              2
            </div>
            <div>
              <h4 className='font-semibold text-gray-900 dark:text-white'>Try the Search</h4>
              <p className='text-gray-600 dark:text-gray-300'>
                Use the search bar to find users by name, email, or company.
              </p>
            </div>
          </div>

          <div className='flex items-start space-x-3'>
            <div className='flex size-8 shrink-0 items-center justify-center rounded-full bg-blue-600 text-sm font-semibold text-white'>
              3
            </div>
            <div>
              <h4 className='font-semibold text-gray-900 dark:text-white'>View User Details</h4>
              <p className='text-gray-600 dark:text-gray-300'>
                Click on any user name to see their detailed information.
              </p>
            </div>
          </div>
        </div>

        <h3 className='mb-4 mt-8 text-xl font-semibold text-gray-900 dark:text-white'>
          Navigation Tips
        </h3>
        <ul className='list-inside list-disc space-y-2 text-gray-600 dark:text-gray-300'>
          <li>Use the navigation bar to switch between sections</li>
          <li>Toggle between light and dark themes using the theme button</li>
          <li>Pagination controls help navigate through large user lists</li>
          <li>Sort columns by clicking on table headers</li>
        </ul>

        <div className='my-6 border-l-4 border-green-400 bg-green-50 p-4 dark:bg-green-900/20'>
          <div className='flex'>
            <div className='ml-3'>
              <p className='text-sm text-green-700 dark:text-green-200'>
                <strong>Ready to explore?</strong> Head to the Users section to start managing
                users!
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
