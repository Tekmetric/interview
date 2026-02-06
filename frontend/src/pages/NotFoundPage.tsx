import { ArrowLeftIcon, HomeIcon } from '@heroicons/react/24/outline';
import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';

export const NotFoundPage: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const suggestions = [
    { path: '/', label: 'Home', description: 'Return to the main dashboard' },
    { path: '/users', label: 'Users', description: 'Manage user accounts' },
    { path: '/help', label: 'Help', description: 'Get help and documentation' },
    { path: '/users/new', label: 'New User', description: 'Create a new user account' },
  ];

  const handleGoBack = () => {
    navigate(-1);
  };

  return (
    <div className='flex min-h-[60vh] items-center justify-center'>
      <div className='max-w-2xl space-y-8 text-center'>
        {/* 404 Header */}
        <div>
          <div className='mb-4 text-9xl font-bold text-gray-300 dark:text-gray-700'>404</div>
          <h1 className='mb-4 text-4xl font-bold text-gray-900 dark:text-white'>Page Not Found</h1>
          <p className='mb-6 text-xl text-gray-600 dark:text-gray-400'>
            The page you&apos;re looking for doesn&apos;t exist or has been moved.
          </p>
        </div>

        <div className='rounded-lg border border-red-200 bg-red-50 p-4 text-left dark:border-red-800 dark:bg-red-900/20'>
          <h3 className='mb-2 font-semibold text-red-900 dark:text-red-100'>Route Not Found</h3>
          <div className='text-sm text-red-700 dark:text-red-200'>
            <div>
              <strong>Attempted path:</strong>{' '}
              <code className='rounded bg-white px-1 dark:bg-gray-800'>{location.pathname}</code>
            </div>
          </div>
        </div>

        {/* Navigation Options */}
        <div className='space-y-6'>
          <div className='flex justify-center space-x-4'>
            <button
              onClick={handleGoBack}
              className='btn-secondary flex items-center'
              aria-label='Go back to previous page'
            >
              <ArrowLeftIcon className='mr-2 size-4' aria-hidden='true' />
              Go Back
            </button>
            <Link to='/' className='btn-primary flex items-center' aria-label='Go to home page'>
              <HomeIcon className='mr-2 size-4' aria-hidden='true' />
              Home
            </Link>
          </div>

          {/* Suggested Pages */}
          <div>
            <h2 className='mb-4 text-xl font-semibold text-gray-900 dark:text-white'>
              Try these pages instead:
            </h2>
            <div className='grid grid-cols-1 gap-4 md:grid-cols-2'>
              {suggestions.map(suggestion => (
                <Link
                  key={suggestion.path}
                  to={suggestion.path}
                  className='block rounded-lg border border-gray-200 bg-white p-4 shadow transition-shadow hover:shadow-md dark:border-gray-700 dark:bg-gray-800'
                  aria-label={`${suggestion.label} - ${suggestion.description}`}
                >
                  <h3 className='font-medium text-gray-900 dark:text-white'>{suggestion.label}</h3>
                  <p className='text-sm text-gray-600 dark:text-gray-400'>
                    {suggestion.description}
                  </p>
                </Link>
              ))}
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className='rounded-lg border border-blue-200 bg-blue-50 p-4 dark:border-blue-800 dark:bg-blue-900/20'>
          <h3 className='mb-3 font-semibold text-blue-900 dark:text-blue-100'>Quick Navigation</h3>
          <div className='flex flex-wrap justify-center gap-2'>
            <Link
              to='/help/getting-started'
              className='btn text-sm px-3 py-1 bg-green-600 hover:bg-green-700 text-white focus:ring-green-500'
            >
              Getting Started
            </Link>
            <Link
              to='/help/user-management'
              className='btn text-sm px-3 py-1 bg-purple-600 hover:bg-purple-700 text-white focus:ring-purple-500'
            >
              User Management
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};
