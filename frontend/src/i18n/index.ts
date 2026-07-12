import type { AppLocale } from '../features/locale/localeSlice';
import enUS from './en-US.json';
import roRO from './ro-RO.json';

// Both catalogs are a few KB, so they are bundled statically. With many
// locales this would become a dynamic import per locale instead.
export const messagesByLocale: Record<AppLocale, Record<string, string>> = {
  'en-US': enUS,
  'ro-RO': roRO,
};
