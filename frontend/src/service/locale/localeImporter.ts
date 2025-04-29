const SUPPORTED_LOCALES = ['en-US', 'ro-RO'] as const;

async function localeImporter(locale: string): Promise<Record<string, string>> {
	if (locale === 'ro-RO') {
		return (await import('./assets/ro-RO.json')).default;
	}

	return (await import('./assets/en-US.json')).default;
}

export default localeImporter;
export { SUPPORTED_LOCALES };
