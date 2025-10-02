import { useState } from 'react'
import { Link, useLocation } from 'wouter'
import { Menu, X } from 'lucide-react'
import {
  WELCOME_LABELS,
  DASHBOARD_LABELS,
  KANBAN_LABELS,
  NAV_LABELS,
} from '@shared/constants'

export function Sidebar() {
  const [location] = useLocation()
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false)

  return (
    <>
      {/* Mobile Header */}
      <div className='fixed top-0 right-0 left-0 z-50 flex h-16 items-center justify-between border-b border-gray-200 bg-white px-4 lg:hidden'>
        <div className='flex items-center gap-3'>
          <div className='flex h-8 w-8 items-center justify-center rounded-lg bg-orange-600'>
            <svg
              xmlns='http://www.w3.org/2000/svg'
              viewBox='0 0 24 24'
              fill='none'
              stroke='currentColor'
              strokeLinecap='round'
              strokeLinejoin='round'
              strokeWidth='2'
              className='h-5 w-5 text-white'
            >
              <path d='M3 3h18v18H3z' />
              <path d='M8 8h8M8 12h8M8 16h8' />
            </svg>
          </div>
          <span className='text-lg font-bold text-gray-900'>{WELCOME_LABELS.TITLE}</span>
        </div>
        <button
          onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
          className='flex h-10 w-10 items-center justify-center rounded-lg text-gray-700 hover:bg-gray-100 focus-visible:ring-2 focus-visible:ring-orange-500 focus-visible:outline-none'
          aria-label={isMobileMenuOpen ? 'Close menu' : 'Open menu'}
          aria-expanded={isMobileMenuOpen}
        >
          {isMobileMenuOpen ? <X className='h-6 w-6' /> : <Menu className='h-6 w-6' />}
        </button>
      </div>

      {/* Mobile Overlay */}
      {isMobileMenuOpen && (
        <div
          className='fixed inset-0 z-40 bg-black/50 lg:hidden'
          onClick={() => setIsMobileMenuOpen(false)}
          aria-hidden='true'
        />
      )}

      {/* Sidebar */}
      <aside
        className={`fixed inset-y-0 left-0 z-50 flex h-screen w-64 flex-col border-r border-gray-200 bg-white transition-transform duration-300 lg:static lg:translate-x-0 ${
          isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
        aria-label='Main navigation'
      >
        <div className='flex h-16 items-center gap-3 border-b border-gray-200 px-6'>
          <div className='flex h-8 w-8 items-center justify-center rounded-lg bg-orange-600'>
            <svg
              xmlns='http://www.w3.org/2000/svg'
              viewBox='0 0 24 24'
              fill='none'
              stroke='currentColor'
              strokeLinecap='round'
              strokeLinejoin='round'
              strokeWidth='2'
              className='h-5 w-5 text-white'
            >
              <path d='M3 3h18v18H3z' />
              <path d='M8 8h8M8 12h8M8 16h8' />
            </svg>
          </div>
          <span className='text-lg font-bold text-gray-900'>{WELCOME_LABELS.TITLE}</span>
        </div>

        <nav className='flex flex-1 flex-col gap-1 p-4'>
          <Link
            href='/'
            className={`flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors focus-visible:ring-2 focus-visible:ring-orange-500 focus-visible:ring-offset-2 focus-visible:outline-none ${
              location === '/'
                ? 'bg-orange-50 text-orange-600'
                : 'text-gray-700 hover:bg-gray-100 hover:text-gray-900'
            }`}
            onClick={() => setIsMobileMenuOpen(false)}
          >
            <svg
              xmlns='http://www.w3.org/2000/svg'
              viewBox='0 0 24 24'
              fill='none'
              stroke='currentColor'
              strokeLinecap='round'
              strokeLinejoin='round'
              strokeWidth='2'
              className='h-5 w-5'
            >
              <rect x='3' y='3' width='7' height='7' />
              <rect x='14' y='3' width='7' height='7' />
              <rect x='14' y='14' width='7' height='7' />
              <rect x='3' y='14' width='7' height='7' />
            </svg>
            {DASHBOARD_LABELS.TITLE}
          </Link>

          <Link
            href='/kanban'
            className={`flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors focus-visible:ring-2 focus-visible:ring-orange-500 focus-visible:ring-offset-2 focus-visible:outline-none ${
              location === '/kanban'
                ? 'bg-orange-50 text-orange-600'
                : 'text-gray-700 hover:bg-gray-100 hover:text-gray-900'
            }`}
            onClick={() => setIsMobileMenuOpen(false)}
          >
            <svg
              xmlns='http://www.w3.org/2000/svg'
              viewBox='0 0 24 24'
              fill='none'
              stroke='currentColor'
              strokeLinecap='round'
              strokeLinejoin='round'
              strokeWidth='2'
              className='h-5 w-5'
            >
              <line x1='8' y1='6' x2='21' y2='6' />
              <line x1='8' y1='12' x2='21' y2='12' />
              <line x1='8' y1='18' x2='21' y2='18' />
              <line x1='3' y1='6' x2='3.01' y2='6' />
              <line x1='3' y1='12' x2='3.01' y2='12' />
              <line x1='3' y1='18' x2='3.01' y2='18' />
            </svg>
            {KANBAN_LABELS.TITLE}
          </Link>

          <a
            href='#'
            className='flex cursor-not-allowed items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-gray-400'
            onClick={(e) => e.preventDefault()}
            aria-disabled='true'
          >
            <svg
              xmlns='http://www.w3.org/2000/svg'
              viewBox='0 0 24 24'
              fill='none'
              stroke='currentColor'
              strokeLinecap='round'
              strokeLinejoin='round'
              strokeWidth='2'
              className='h-5 w-5'
            >
              <line x1='18' y1='20' x2='18' y2='10' />
              <line x1='12' y1='20' x2='12' y2='4' />
              <line x1='6' y1='20' x2='6' y2='14' />
            </svg>
            {NAV_LABELS.REPORTS}
          </a>
        </nav>

        <div className='border-t border-gray-200 p-4'>
          <div className='flex items-center gap-3 rounded-lg px-3 py-2 transition-colors hover:bg-gray-100'>
            <div className='flex h-8 w-8 items-center justify-center rounded-full bg-orange-600 text-sm font-semibold text-white'>
              DU
            </div>
            <div className='flex-1'>
              <p className='text-sm font-medium text-gray-900'>
                {WELCOME_LABELS.DEMO_USER_NAME}
              </p>
              <p className='text-xs text-gray-500'>{WELCOME_LABELS.DEMO_USER_EMAIL}</p>
            </div>
          </div>
        </div>
      </aside>
    </>
  )
}
