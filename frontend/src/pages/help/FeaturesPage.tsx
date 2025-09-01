import {
  CogIcon,
  CursorArrowRaysIcon as TargetIcon,
  RocketLaunchIcon as RocketIcon,
  WrenchScrewdriverIcon as ToolIcon,
} from '@heroicons/react/24/outline';
import React from 'react';

export const FeaturesPage: React.FC = () => {
  return (
    <div className='space-y-6'>
      <h2 className='text-2xl font-bold text-gray-900 dark:text-white'>Technical Features</h2>

      <div className='prose dark:prose-invert max-w-none'>
        <p className='text-lg text-gray-600 dark:text-gray-300'>
          This production-ready React application demonstrates modern development practices,
          comprehensive testing, accessibility standards, and advanced form handling with server
          state management.
        </p>

        <h3 className='mb-4 mt-8 flex items-center text-xl font-semibold text-gray-900 dark:text-white'>
          <RocketIcon className='mr-2 size-4' aria-hidden='true' />
          Core Technologies
        </h3>

        <div className='my-6 grid grid-cols-1 gap-4 md:grid-cols-2'>
          <div className='rounded-lg bg-blue-50 p-4 dark:bg-blue-900/20'>
            <h4 className='mb-2 font-semibold text-blue-900 dark:text-blue-100'>React & Router</h4>
            <ul className='space-y-1 text-sm text-blue-800 dark:text-blue-200'>
              <li>• React 18 with functional components</li>
              <li>• React Router v7 with nested routing</li>
              <li>• Dynamic route parameters</li>
              <li>• URL state synchronization</li>
              <li>• Component-based architecture</li>
            </ul>
          </div>

          <div className='rounded-lg bg-green-50 p-4 dark:bg-green-900/20'>
            <h4 className='mb-2 font-semibold text-green-900 dark:text-green-100'>TypeScript</h4>
            <ul className='space-y-1 text-sm text-green-800 dark:text-green-200'>
              <li>• TypeScript 5.0+ with strict mode</li>
              <li>• Full type safety throughout</li>
              <li>• Advanced interface definitions</li>
              <li>• Type-safe API calls</li>
              <li>• Enhanced IntelliSense support</li>
            </ul>
          </div>

          <div className='rounded-lg bg-purple-50 p-4 dark:bg-purple-900/20'>
            <h4 className='mb-2 font-semibold text-purple-900 dark:text-purple-100'>
              Styling & UI
            </h4>
            <ul className='space-y-1 text-sm text-purple-800 dark:text-purple-200'>
              <li>• Tailwind CSS 3.4 utility-first</li>
              <li>• Advanced theme system (light/dark/system)</li>
              <li>• Responsive design patterns</li>
              <li>• Hero Icons integration</li>
              <li>• Modern component styling</li>
            </ul>
          </div>

          <div className='rounded-lg bg-amber-50 p-4 dark:bg-amber-900/20'>
            <h4 className='mb-2 font-semibold text-amber-900 dark:text-amber-100'>
              State Management
            </h4>
            <ul className='space-y-1 text-sm text-amber-800 dark:text-amber-200'>
              <li>• React Context for global state</li>
              <li>• React Query for server state</li>
              <li>• Custom hooks for reusable logic</li>
              <li>• URL-based state management</li>
              <li>• LocalStorage persistence</li>
            </ul>
          </div>
        </div>

        <h3 className='mb-4 mt-8 flex items-center text-xl font-semibold text-gray-900 dark:text-white'>
          <CogIcon className='mr-2 size-4' aria-hidden='true' />
          Key Features
        </h3>

        <div className='space-y-4'>
          <div className='border-l-4 border-blue-500 pl-4'>
            <h4 className='mb-2 font-semibold text-gray-900 dark:text-white'>User Management</h4>
            <ul className='list-inside list-disc space-y-1 text-gray-600 dark:text-gray-300'>
              <li>CRUD operations with optimistic updates</li>
              <li>Advanced search across multiple fields</li>
              <li>Status filtering (Active/Inactive)</li>
              <li>Table sorting with visual indicators</li>
              <li>Pagination with configurable page sizes</li>
              <li>Individual user detail pages with forms</li>
            </ul>
          </div>

          <div className='border-l-4 border-green-500 pl-4'>
            <h4 className='mb-2 font-semibold text-gray-900 dark:text-white'>
              Form Validation & Data Handling
            </h4>
            <ul className='list-inside list-disc space-y-1 text-gray-600 dark:text-gray-300'>
              <li>Zod schema-based validation with TypeScript inference</li>
              <li>React Query for server state management</li>
              <li>Real-time field validation on blur</li>
              <li>Form error handling with user-friendly messages</li>
              <li>Caching and background data synchronization</li>
              <li>Optimistic updates for better UX</li>
            </ul>
          </div>

          <div className='border-l-4 border-purple-500 pl-4'>
            <h4 className='mb-2 font-semibold text-gray-900 dark:text-white'>Theme System</h4>
            <ul className='list-inside list-disc space-y-1 text-gray-600 dark:text-gray-300'>
              <li>Three-mode theming: Light, Dark, System</li>
              <li>Modern segmented control toggle interface</li>
              <li>Automatic system preference detection</li>
              <li>Persistent theme storage across sessions</li>
              <li>Smooth transitions between theme modes</li>
              <li>Context-based theme management</li>
            </ul>
          </div>

          <div className='border-l-4 border-rose-500 pl-4'>
            <h4 className='mb-2 font-semibold text-gray-900 dark:text-white'>Accessibility & UX</h4>
            <ul className='list-inside list-disc space-y-1 text-gray-600 dark:text-gray-300'>
              <li>Comprehensive ARIA labels and semantic HTML</li>
              <li>Keyboard navigation support</li>
              <li>Screen reader optimizations</li>
              <li>Focus management in modals and forms</li>
              <li>High contrast and responsive design</li>
            </ul>
          </div>
        </div>

        <h3 className='mb-4 mt-8 flex items-center text-xl font-semibold text-gray-900 dark:text-white'>
          <ToolIcon className='mr-2 size-4' aria-hidden='true' />
          Development Stack
        </h3>
        <div className='rounded-lg bg-gray-50 p-4 dark:bg-gray-800'>
          <div className='grid grid-cols-2 gap-4 text-sm md:grid-cols-4'>
            <div>
              <h5 className='font-semibold text-gray-900 dark:text-white'>Frontend</h5>
              <ul className='text-gray-600 dark:text-gray-300'>
                <li>• React 18</li>
                <li>• TypeScript 5.0+</li>
                <li>• React Router v7</li>
                <li>• Vite</li>
              </ul>
            </div>
            <div>
              <h5 className='font-semibold text-gray-900 dark:text-white'>Styling</h5>
              <ul className='text-gray-600 dark:text-gray-300'>
                <li>• Tailwind CSS 3.4</li>
                <li>• PostCSS</li>
                <li>• Hero Icons</li>
                <li>• Responsive Design</li>
              </ul>
            </div>
            <div>
              <h5 className='font-semibold text-gray-900 dark:text-white'>Data & Forms</h5>
              <ul className='text-gray-600 dark:text-gray-300'>
                <li>• React Query</li>
                <li>• Zod Validation</li>
                <li>• API Integration</li>
                <li>• State Management</li>
              </ul>
            </div>
            <div>
              <h5 className='font-semibold text-gray-900 dark:text-white'>Quality</h5>
              <ul className='text-gray-600 dark:text-gray-300'>
                <li>• ESLint + Prettier</li>
                <li>• Vitest + RTL</li>
                <li>• TypeScript Strict</li>
                <li>• Accessibility</li>
              </ul>
            </div>
          </div>
        </div>

        <div className='my-6 grid grid-cols-1 gap-4 md:grid-cols-2'>
          <div className='rounded-lg bg-indigo-50 p-4 dark:bg-indigo-900/20'>
            <h4 className='mb-2 font-semibold text-indigo-900 dark:text-indigo-100'>
              Testing & Quality
            </h4>
            <ul className='space-y-1 text-sm text-indigo-800 dark:text-indigo-200'>
              <li>• Comprehensive unit tests with Vitest</li>
              <li>• React Testing Library integration</li>
              <li>• Component and hook testing</li>
              <li>• Coverage reporting and UI</li>
              <li>• ESLint with accessibility rules</li>
              <li>• Prettier code formatting</li>
            </ul>
          </div>

          <div className='rounded-lg bg-emerald-50 p-4 dark:bg-emerald-900/20'>
            <h4 className='mb-2 font-semibold text-emerald-900 dark:text-emerald-100'>
              Developer Experience
            </h4>
            <ul className='space-y-1 text-sm text-emerald-800 dark:text-emerald-200'>
              <li>• Hot module replacement with Vite</li>
              <li>• TypeScript strict mode configuration</li>
              <li>• VS Code settings and extensions</li>
              <li>• Auto-formatting on save</li>
              <li>• Import sorting and organization</li>
              <li>• React Query DevTools integration</li>
            </ul>
          </div>
        </div>

        <div className='mt-8 rounded-lg bg-gradient-to-r from-blue-50 to-purple-50 p-6 dark:from-blue-900/20 dark:to-purple-900/20'>
          <h4 className='mb-2 flex items-center text-lg font-semibold text-gray-900 dark:text-white'>
            <TargetIcon className='mr-2 size-4' aria-hidden='true' />
            Production-Ready Architecture
          </h4>
          <p className='text-gray-600 dark:text-gray-300'>
            This application demonstrates enterprise-level React development with comprehensive
            testing, accessibility compliance, advanced form validation, server state management,
            and modern development tooling. Built following best practices for maintainability,
            scalability, and user experience.
          </p>
        </div>
      </div>
    </div>
  );
};
