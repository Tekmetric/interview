import 'styled-components';

interface TypographyInterface
	extends Pick<
		CSSProperties,
		'fontFamily' | 'fontSize' | 'fontWeight' | 'lineHeight' | 'letterSpacing'
	> {}

interface CustomTheme {
	// background color
	ui01: string;
	ui01Hover: string;
	textColor01: string;

	// accent color
	ui02: string;
	ui02Hover: string;
	textColor02: string;

	// destructive color
	ui03: string;
	ui03Hover: string;
	textColor03: string;
	textColorDestructive: string;

	focusColor: string;
	borderColor: string;

	contentTypography: TypographyInterface;
	titleTypography: TypographyInterface;
}

declare module 'styled-components' {
	// eslint-disable-next-line @typescript-eslint/no-empty-interface
	export interface DefaultTheme extends CustomTheme {}
}
