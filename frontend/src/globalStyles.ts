import { createGlobalStyle } from 'styled-components';

const GlobalStyles = createGlobalStyle`
	* {
		box-sizing: border-box;
		font-variant-numeric: tabular-nums;
	}

	html, body, #root {
		margin: 0;
		padding: 0;
		height: 100%;
		width: 100%;
		overflow: hidden;
	}

	body {
		${({ theme }) => theme.contentTypography};
		background-color: ${({ theme }) => theme.ui01};
		color: ${({ theme }) => theme.textColor01};
	}
`;

export default GlobalStyles;
