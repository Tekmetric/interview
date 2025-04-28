import { useCallback, useEffect, useMemo, useState } from 'react';
import { ThemeProvider } from 'styled-components';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';

import themes, { type ThemeName } from './utils/theme';
import { getLocale, getThemeColor } from './utils/localStorageHandlers';
import { LOCAL_STORAGE_KEYS } from './utils/constants';
import localeImporter, {
	SUPPORTED_LOCALES,
} from './service/locale/localeImporter';
import createStore from './store';
import GlobalStyles from './globalStyles';

function AppWithProviders() {
	const [themeName, setThemeName] = useState<ThemeName>(getThemeColor());
	const [locale, setLocale] =
		useState<(typeof SUPPORTED_LOCALES)[number]>(getLocale());
	const [messages, setMessages] = useState<Record<string, string>>();

	const handleThemeSwitch = useCallback(() => {
		const newThemeName = themeName === 'dark' ? 'light' : 'dark';

		setThemeName(newThemeName);
		localStorage.setItem(LOCAL_STORAGE_KEYS.themeName, newThemeName);
	}, [themeName]);

	const handleLocaleSwitch = useCallback(() => {
		const currentIndex = SUPPORTED_LOCALES.indexOf(
			locale as (typeof SUPPORTED_LOCALES)[number]
		);
		const newIndex = (currentIndex + 1) % SUPPORTED_LOCALES.length;
		const newLocale = SUPPORTED_LOCALES[newIndex];

		if (!newLocale) {
			return;
		}

		setLocale(newLocale);
		localStorage.setItem(LOCAL_STORAGE_KEYS.locale, newLocale);
	}, [locale]);

	useEffect(() => {
		localeImporter(locale).then((messages) => {
			setMessages(messages);
		});
	}, [locale]);

	if (!messages) {
		return null;
	}

	return (
		<ThemeProvider theme={themes[themeName]}>
			<GlobalStyles />

			<IntlProvider
				locale={locale}
				messages={messages}
				defaultLocale={SUPPORTED_LOCALES[0]}
			>
				boo
			</IntlProvider>
		</ThemeProvider>
	);
}

function App() {
	const store = useMemo(() => createStore(), []);

	return (
		<Provider store={store}>
			<AppWithProviders />
		</Provider>
	);
}

export default App;
