import styled from 'styled-components';

const Text = styled.div<{ shouldShowBold?: boolean }>`
	${({ theme }) => theme.contentTypography};
	${({ shouldShowBold }) => shouldShowBold && 'font-weight: bold;'}
	width: 100%;
	max-width: 100%;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
`;

const Link = styled.a`
	${({ theme }) => theme.contentTypography};
	text-decoration: none;
	cursor: pointer;
	color: ${({ theme }) => theme.focusColor};
	width: 100%;
	max-width: 100%;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
`;

export { Text, Link };
