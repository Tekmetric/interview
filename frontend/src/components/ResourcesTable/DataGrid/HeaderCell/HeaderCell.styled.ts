import styled from 'styled-components';

const StyledHeaderCell = styled.div`
	display: flex;
	align-items: center;
	user-select: none;
	width: 100%;
	height: auto;
	overflow: hidden;
	background-color: ${({ theme }) => theme.ui02};
	color: ${({ theme }) => theme.textColor02};
	padding: ${({ theme }) => theme.spacing04};
`;

const HeaderActiveCell = styled.button`
	all: unset;
	box-sizing: border-box;
	cursor: pointer;
	display: flex;
	gap: ${({ theme }) => theme.spacing02};
	align-items: center;
	user-select: none;
	width: 100%;
	height: auto;
	overflow: hidden;
	background-color: ${({ theme }) => theme.ui02};
	color: ${({ theme }) => theme.textColor02};
	padding: ${({ theme }) => theme.spacing04};

	&:focus-visible {
		box-shadow: 0 0 2px 2px inset ${({ theme }) => theme.focusColor};
	}
`;

const Text = styled.div`
	${({ theme }) => theme.contentTypography};
	width: 100%;
	max-width: 100%;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
`;

const IconWrapper = styled.div<{ shouldInvert?: boolean }>`
	display: flex;
	flex-shrink: 0;
	flex-grow: 0;
	width: 24px;
	height: 24px;
	align-items: center;
	justify-content: center;
	${({ shouldInvert }) =>
		shouldInvert &&
		`
	transform: rotate(180deg);
`}
`;

export { StyledHeaderCell, HeaderActiveCell, Text, IconWrapper };
