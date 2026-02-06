import { BookOpenIcon as BookIcon, UsersIcon } from '@heroicons/react/24/outline';
import React from 'react';
import { Link } from 'react-router-dom';

import { useTheme } from '../contexts/ThemeContext';

export const HomePage: React.FC = () => {
  const { actualTheme } = useTheme();

  // Conditional gradient classes based on theme
  const gradientClass =
    actualTheme === 'dark'
      ? 'bg-gradient-to-r from-gray-900 via-blue-900 to-gray-900'
      : 'bg-gradient-to-r from-blue-600 via-blue-500 to-indigo-600';

  const features = [
    {
      title: 'User Management',
      description: 'Complete CRUD operations with search and filtering',
      path: '/users',
      icon: UsersIcon,
    },
    {
      title: 'Help & Documentation',
      description: 'Get help and learn about features',
      path: '/help',
      icon: BookIcon,
    },
  ];

  return (
    <div className='space-y-8'>
      {/* Hero Section */}
      <div className={`${gradientClass} rounded-lg py-12 text-center text-white`}>
        <h1 className='mb-4 text-4xl font-bold'>User Management Dashboard</h1>
        <p className='mx-auto mb-6 max-w-2xl text-xl'>
          A comprehensive platform for managing users with modern React architecture.
        </p>
        <div className='flex justify-center space-x-4'>
          <Link
            to='/users'
            className='btn bg-white text-blue-600 hover:bg-gray-100 dark:bg-gray-800 dark:text-blue-400 dark:hover:bg-gray-700 focus:ring-blue-500'
          >
            Manage Users
          </Link>
          <Link
            to='/help'
            className='btn border border-white bg-transparent text-white hover:bg-white hover:text-blue-600 dark:border-gray-300 dark:hover:bg-gray-300 dark:hover:text-blue-600 focus:ring-white'
          >
            Get Help
          </Link>
        </div>
      </div>

      {/* Features Grid */}
      <div className='grid gap-6 md:grid-cols-2'>
        {features.map((feature, index) => (
          <Link
            key={index}
            to={feature.path}
            className='rounded-lg bg-white p-6 shadow-md transition-shadow hover:shadow-lg dark:bg-gray-800'
            aria-label={`${feature.title} - ${feature.description}`}
          >
            <div className='mb-4 flex items-center'>
              <span className='mr-3' aria-hidden='true'>
                <feature.icon className='size-6 text-blue-500' />
              </span>
              <h3 className='text-lg font-semibold text-gray-900 dark:text-white'>
                {feature.title}
              </h3>
            </div>
            <p className='mb-4 text-gray-600 dark:text-gray-300'>{feature.description}</p>
            <span className='btn-primary inline-block text-sm' aria-hidden='true'>
              Access →
            </span>
          </Link>
        ))}
      </div>
    </div>
  );
};
