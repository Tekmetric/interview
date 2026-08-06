import { Outlet } from 'react-router-dom';
import Header from '../components/Header';

export default function RootLayout() {
  return (
    <div className="flex min-h-screen flex-col bg-canvas text-ink">
      {/* Skip link: lets keyboard users jump past the header to the content. */}
      <a
        href="#main"
        className="sr-only focus:not-sr-only focus:absolute focus:left-4 focus:top-4 focus:z-50 focus:rounded-md focus:bg-accent focus:px-3 focus:py-2 focus:text-canvas"
      >
        Skip to content
      </a>

      <Header />

      <main id="main" className="mx-auto w-full max-w-[1600px] flex-1 px-4 py-8 sm:px-6">
        <Outlet />
      </main>

      <footer className="border-t border-line px-4 py-6 text-center text-sm text-muted">
        <p>
          Collection data by{' '}
          <a
            href="https://www.metmuseum.org/"
            className="text-accent hover:underline"
            target="_blank"
            rel="noreferrer"
          >
            The Met
          </a>{' '}
          · Typeface: Cooper Hewitt
        </p>
      </footer>
    </div>
  );
}
