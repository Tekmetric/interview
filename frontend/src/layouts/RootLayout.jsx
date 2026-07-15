import { Outlet, NavLink } from 'react-router-dom';
import Header from '../components/Header';
import { useTranslation } from '../i18n/LocaleProvider';

export default function RootLayout() {
  const { t } = useTranslation();
  return (
    <div className="flex min-h-screen flex-col bg-canvas text-ink">
      {/* Skip link: lets keyboard users jump past the header to the content. */}
      <a
        href="#main"
        className="sr-only focus:not-sr-only focus:absolute focus:left-4 focus:top-4 focus:z-50 focus:rounded-md focus:bg-accent focus:px-3 focus:py-2 focus:text-canvas"
      >
        {t('a11y.skipToContent')}
      </a>

      <Header />

      <main id="main" className="mx-auto w-full max-w-[1600px] flex-1 px-4 py-8 sm:px-6">
        <Outlet />
      </main>

      <footer className="border-t border-line px-4 py-6 text-center text-sm text-muted">
        <p className="mb-2">
          <NavLink
            to="/how-i-built-this"
            className={({ isActive }) =>
              `hover:text-accent ${isActive ? 'text-accent' : ''}`
            }
          >
            {t('nav.howBuilt')}
          </NavLink>
        </p>
        <p>
          {t('footer.dataBy', { provider: '' })}
          <a
            href="https://www.metmuseum.org/"
            className="text-accent hover:underline"
            target="_blank"
            rel="noreferrer"
          >
            The Met
          </a>{' '}
          · {t('footer.typeface', { name: 'Cooper Hewitt' })}
        </p>
      </footer>
    </div>
  );
}
