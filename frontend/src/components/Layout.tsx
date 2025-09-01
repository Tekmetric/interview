import React from 'react';
import { Outlet } from 'react-router-dom';

import Footer from './Footer';
import Header from './Header';
import { Navigation } from './Navigation';

export const Layout: React.FC = () => {
  return (
    <div className='flex min-h-screen flex-col bg-gray-50 dark:bg-gray-900'>
      {/* Skip Navigation Link */}
      <a
        href='#main-content'
        className='sr-only focus:not-sr-only focus:absolute focus:top-4 focus:left-4 focus:z-50 rounded bg-blue-600 px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-blue-500'
        aria-label='Skip to main content'
      >
        Skip to main content
      </a>

      <Header />
      <Navigation />

      <main
        id='main-content'
        className='container mx-auto flex-1 px-4 py-6'
        role='main'
        aria-label='Main content'
      >
        {/* Outlet renders the matched child route */}
        <Outlet />
      </main>

      <Footer />
    </div>
  );
};
