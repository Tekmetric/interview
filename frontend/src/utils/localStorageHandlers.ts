import { SUPPORTED_LOCALES } from 'src/service/locale/localeImporter';

import { LOCAL_STORAGE_KEYS } from './constants';
import { type ThemeName } from './theme';

function getThemeColor(): ThemeName {
	const themeName = localStorage.getItem(LOCAL_STORAGE_KEYS.themeName);

	return themeName === 'dark' ? 'dark' : 'light';
}

function getLocale(): (typeof SUPPORTED_LOCALES)[number] {
	const locale = localStorage.getItem(LOCAL_STORAGE_KEYS.locale);

	return SUPPORTED_LOCALES.includes(locale as any)
		? (locale as (typeof SUPPORTED_LOCALES)[number])
		: SUPPORTED_LOCALES[0];
}

export { getThemeColor, getLocale };
