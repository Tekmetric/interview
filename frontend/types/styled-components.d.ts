import 'styled-components';

interface CustomTheme {
	// extend this with tokens to use inside the app
}

declare module 'styled-components' {
	// eslint-disable-next-line @typescript-eslint/no-empty-interface
	export interface DefaultTheme extends CustomTheme {}
}
