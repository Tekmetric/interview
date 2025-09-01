import React from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';

export const HelpPage: React.FC = () => {
  const location = useLocation();
  const isMainHelpPage = location.pathname === '/help';

  return (
    <div className='space-y-6'>
      <div className='rounded-lg bg-white p-6 shadow dark:bg-gray-800'>
        <h1 className='mb-4 text-3xl font-bold text-gray-900 dark:text-white'>
          Help & Documentation
        </h1>
        <p className='mb-6 text-gray-600 dark:text-gray-300'>
          Learn how to use the user management dashboard effectively.
        </p>

        {/* Navigation for help sections */}
        <nav className='mb-6 border-b border-gray-200 dark:border-gray-700'>
          <div className='flex flex-wrap gap-2 sm:gap-0 sm:space-x-8'>
            <NavLink
              to='/help'
              end
              className={({ isActive }) =>
                `py-2 px-1 border-b-2 font-medium text-sm whitespace-nowrap ${
                  isActive
                    ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 dark:text-gray-400 dark:hover:text-gray-300'
                }`
              }
            >
              Overview
            </NavLink>
            <NavLink
              to='/help/getting-started'
              className={({ isActive }) =>
                `py-2 px-1 border-b-2 font-medium text-sm whitespace-nowrap ${
                  isActive
                    ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 dark:text-gray-400 dark:hover:text-gray-300'
                }`
              }
            >
              Getting Started
            </NavLink>
            <NavLink
              to='/help/user-management'
              className={({ isActive }) =>
                `py-2 px-1 border-b-2 font-medium text-sm whitespace-nowrap ${
                  isActive
                    ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 dark:text-gray-400 dark:hover:text-gray-300'
                }`
              }
            >
              User Management
            </NavLink>
            <NavLink
              to='/help/features'
              className={({ isActive }) =>
                `py-2 px-1 border-b-2 font-medium text-sm whitespace-nowrap ${
                  isActive
                    ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 dark:text-gray-400 dark:hover:text-gray-300'
                }`
              }
            >
              Features
            </NavLink>
          </div>
        </nav>

        {/* Help content area */}
        <div className='min-h-96'>{isMainHelpPage ? <HelpOverview /> : <Outlet />}</div>
      </div>
    </div>
  );
};

const HelpOverview: React.FC = () => {
  return (
    <div className='space-y-6'>
      <div className='prose dark:prose-invert max-w-none'>
        <p className='text-lg text-gray-600 dark:text-gray-300'>
          Welcome to the User Management Dashboard! This application demonstrates modern React
          development with user management capabilities.
        </p>
      </div>

      <div className='grid grid-cols-1 gap-6 md:grid-cols-3'>
        <div className='rounded-lg bg-blue-50 p-6 dark:bg-blue-900/20'>
          <h3 className='mb-2 text-lg font-semibold text-blue-900 dark:text-blue-100'>
            Quick Start
          </h3>
          <p className='mb-4 text-blue-700 dark:text-blue-200'>
            Learn the basics in a few simple steps.
          </p>
          <NavLink
            to='/help/getting-started'
            className='font-medium text-blue-600 hover:text-blue-800 dark:text-blue-400 dark:hover:text-blue-300'
          >
            Get Started →
          </NavLink>
        </div>

        <div className='rounded-lg bg-green-50 p-6 dark:bg-green-900/20'>
          <h3 className='mb-2 text-lg font-semibold text-green-900 dark:text-green-100'>
            User Management
          </h3>
          <p className='mb-4 text-green-700 dark:text-green-200'>
            How to add, edit, and delete users.
          </p>
          <NavLink
            to='/help/user-management'
            className='font-medium text-green-600 hover:text-green-800 dark:text-green-400 dark:hover:text-green-300'
          >
            Learn More →
          </NavLink>
        </div>

        <div className='rounded-lg bg-purple-50 p-6 dark:bg-purple-900/20'>
          <h3 className='mb-2 text-lg font-semibold text-purple-900 dark:text-purple-100'>
            Technical Features
          </h3>
          <p className='mb-4 text-purple-700 dark:text-purple-200'>
            Explore the technology and architecture.
          </p>
          <NavLink
            to='/help/features'
            className='font-medium text-purple-600 hover:text-purple-800 dark:text-purple-400 dark:hover:text-purple-300'
          >
            Explore →
          </NavLink>
        </div>
      </div>
    </div>
  );
};
