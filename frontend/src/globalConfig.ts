import { useEffect } from 'react';
import { useIntl } from 'react-intl';
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

function TitleHandler() {
	const { formatMessage } = useIntl();

	useEffect(() => {
		document.title = formatMessage({ id: 'LEARNING_RESOURCES' });
	}, [formatMessage]);

	return null;
}

export { TitleHandler };
export default GlobalStyles;
