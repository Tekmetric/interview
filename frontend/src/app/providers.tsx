import { useEffect, type ReactNode } from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { ThemeProvider } from 'styled-components';

import { messagesByLocale } from '../i18n';
import { GlobalStyle } from '../theme/GlobalStyle';
import { themesByMode } from '../theme';
import { useAppSelector } from './hooks';
import { store as defaultStore, type AppStore } from './store';

interface AppProvidersProps {
  children: ReactNode;
  store?: AppStore;
}

// Inner component so it can read the store via hooks.
function ThemedIntl({ children }: { children: ReactNode }) {
  const mode = useAppSelector((state) => state.theme.mode);
  const locale = useAppSelector((state) => state.locale.locale);

  // Screen readers pick pronunciation from the document language.
  useEffect(() => {
    document.documentElement.lang = locale;
  }, [locale]);

  return (
    <ThemeProvider theme={themesByMode[mode]}>
      <IntlProvider locale={locale} messages={messagesByLocale[locale]} defaultLocale="en-US">
        <GlobalStyle />
        {children}
      </IntlProvider>
    </ThemeProvider>
  );
}

export function AppProviders({ children, store = defaultStore }: AppProvidersProps) {
  return (
    <Provider store={store}>
      <ThemedIntl>{children}</ThemedIntl>
    </Provider>
  );
}
