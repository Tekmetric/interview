import { useEffect, useState, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { VariableSizeList } from 'react-window';
import { useAppDispatch, useAppSelector } from './store/hooks';
import { fetchPokemonData, setSearchTerm } from './store/pokemonSlice';
import { selectFilteredPokemon, selectPokemonLoading, selectPokemonError, selectSearchTerm, selectIsDarkMode } from './store/selectors';
import Table from './components/Table';
import ErrorBoundary from './components/ErrorBoundary';
import LanguageSwitcher from './components/LanguageSwitcher';
import DarkModeToggle from './components/DarkModeToggle';
import { backgroundStyle, darkBackgroundStyle, classes } from './lib/styles';

function App() {
  const { t } = useTranslation();
  const dispatch = useAppDispatch();

  // Redux state using memoized selectors
  const filteredPokemon = useAppSelector(selectFilteredPokemon);
  const loading = useAppSelector(selectPokemonLoading);
  const error = useAppSelector(selectPokemonError);
  const searchQuery = useAppSelector(selectSearchTerm);
  const isDark = useAppSelector(selectIsDarkMode);

  // Local state
  const [windowHeight, setWindowHeight] = useState(window.innerHeight);
  const [windowWidth, setWindowWidth] = useState(window.innerWidth);

  // Refs
  const listRef = useRef<VariableSizeList>(null);
  const searchInputRef = useRef<HTMLInputElement>(null);
  const rowHeights = useRef<Record<number, number>>({});

  const isMobile = windowWidth < 768;

  useEffect(() => {
    const handleResize = () => {
      setWindowHeight(window.innerHeight);
      setWindowWidth(window.innerWidth);
    };

    // Override find-in-browser as it wouldn't work in a virtualized window
    const handleKeyDown = (e: KeyboardEvent) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 'f') {
        e.preventDefault();
        searchInputRef.current?.focus();
      }
    };

    window.addEventListener('resize', handleResize);
    window.addEventListener('keydown', handleKeyDown);

    return () => {
      window.removeEventListener('resize', handleResize);
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, []);

  const setRowHeight = (index: number, size: number) => {
    if (rowHeights.current[index] !== size) {
      rowHeights.current[index] = size;
      if (listRef.current) {
        listRef.current.resetAfterIndex(index);
      }
    }
  };

  // Fetch Pokemon data on mount
  useEffect(() => {
    dispatch(fetchPokemonData());
  }, [dispatch]);

  useEffect(() => {
    // Focus search input when data is loaded
    if (!loading && !error && searchInputRef.current) {
      searchInputRef.current.focus();
    }
  }, [loading, error]);

  if (loading) {
    return (
      <div className={classes.loadingContainer}>
        <img src="/pokeball.svg" alt={t('app.loading')} className={classes.loadingImage} />
        <div className={classes.loadingText}>{t('app.loading')}</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className={classes.errorContainer}>
        <div className={classes.errorIcon}>⚠️</div>
        <div className={classes.errorTitle}>{t('app.error.title')}</div>
        <div className={classes.errorMessage}>
          {error}
        </div>
        <button
          onClick={() => window.location.reload()}
          className={classes.errorButton}
        >
          {t('app.error.retry')}
        </button>
      </div>
    );
  }

  return (
    <div className={classes.container(isMobile)} style={isDark ? darkBackgroundStyle : backgroundStyle}>
      <a
        href="#main-content"
        className="sr-only focus:not-sr-only focus:absolute focus:top-4 focus:left-4 focus:z-50 focus:px-4 focus:py-2 focus:bg-blue-600 focus:text-white focus:rounded"
      >
        {t('app.skipToContent')}
      </a>
      <div className="absolute top-4 right-4 flex items-center gap-2 z-10">
        <DarkModeToggle />
        <LanguageSwitcher />
      </div>
      <div className={classes.card} id="main-content">
        <div className={classes.header(isMobile)}>
          <div className="mb-4">
            <h1 className={classes.title(isMobile)}>
              {t('app.title')}
            </h1>
            <p className={classes.tagline(isMobile)}>
              {t('app.tagline')}
            </p>
          </div>
          <input
            ref={searchInputRef}
            type="text"
            placeholder={t('app.search')}
            value={searchQuery}
            onChange={(e) => dispatch(setSearchTerm(e.target.value))}
            className={classes.searchInput}
            aria-label={t('app.search')}
            tabIndex={0}
          />
        </div>
        <ErrorBoundary
          fallback={(error, reset) => (
            <div className="flex-1 flex flex-col items-center justify-center p-8">
              <span className="text-4xl mb-4" role="img" aria-label="Error">😵</span>
              <h2 className="text-xl font-bold text-gray-800 mb-2">
                Something went wrong while rendering the table
              </h2>
              <p className="text-gray-600 mb-4 text-center">
                {error?.message || 'An unexpected error occurred'}
              </p>
              <button
                onClick={reset}
                className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                Try Again
              </button>
            </div>
          )}
        >
          <Table
            filteredPokemon={filteredPokemon}
            isMobile={isMobile}
            windowHeight={windowHeight}
            listRef={listRef}
            rowHeights={rowHeights}
            setRowHeight={setRowHeight}
          />
        </ErrorBoundary>
        <div className={classes.footer(isMobile)}>
          {t('app.footer', { year: new Date().getFullYear() })}
        </div>
      </div>
    </div>
  );
}

export default App;
