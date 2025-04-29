import { type DefaultTheme } from 'styled-components';

const contentTypography: DefaultTheme['contentTypography'] = {
	fontFamily: 'Inter, sans-serif',
	fontSize: '1rem',
	fontWeight: '400',
	lineHeight: '1.5rem',
	letterSpacing: '0.5px',
};

const titleTypography: DefaultTheme['titleTypography'] = {
	fontFamily: 'Inter, sans-serif',
	fontSize: '1.5rem',
	fontWeight: '600',
	lineHeight: '2rem',
	letterSpacing: '0.5px',
};

const spacing = {
	spacing01: '2px',
	spacing02: '4px',
	spacing03: '8px',
	spacing04: '12px',
	spacing05: '16px',
};

const lightTheme: DefaultTheme = {
	// background color
	ui01: '#ffffff',
	ui01Hover: '#f5f5f5',
	textColor01: '#000000',

	// accent color
	ui02: '#8C1AF6',
	ui02Hover: '#7A0EDB',
	textColor02: '#ffffff',

	// destructive color
	ui03: '#FF3D00',
	ui03Hover: '#FF3D00',
	textColor03: '#ffffff',
	textColorDestructive: '#FF3D00',

	focusColor: '#0000EE',
	borderColor: '#E0E0E0',

	contentTypography,
	titleTypography,

	...spacing,

	themeName: 'light',
};

const darkTheme: DefaultTheme = {
	// background color
	ui01: '#1E1E2F',
	ui01Hover: '#2A2A3D',
	textColor01: '#ffffff',

	// accent color
	ui02: '#8C1AF6',
	ui02Hover: '#7A0EDB',
	textColor02: '#ffffff',

	// destructive color
	ui03: '#FF3D00',
	ui03Hover: '#FF3D00',
	textColor03: '#ffffff',
	textColorDestructive: '#FF3D00',

	focusColor: '#0000EE',
	borderColor: '#E0E0E0',

	contentTypography,
	titleTypography,

	...spacing,

	themeName: 'dark',
};

const themes = {
	light: lightTheme,
	dark: darkTheme,
};

export type ThemeName = keyof typeof themes;
export default themes;
