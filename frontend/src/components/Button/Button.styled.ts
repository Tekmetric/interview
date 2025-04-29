import styled from 'styled-components';

const Button = styled.button<{
	isRound?: boolean;
	buttonType?: 'secondary' | 'destructive';
}>`
	all: unset;
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: pointer;
	padding: ${({ theme, isRound }) =>
		isRound ? theme.spacing03 : `${theme.spacing03} ${theme.spacing04}`};
	border: 1px solid ${({ theme }) => theme.borderColor};
	border-radius: ${({ theme, isRound }) => (isRound ? '50%' : theme.spacing02)};
	background-color: ${({ theme, buttonType = 'secondary' }) =>
		buttonType === 'secondary' ? theme.ui01 : theme.ui03};
	color: ${({ theme, buttonType = 'secondary' }) =>
		buttonType === 'secondary' ? theme.textColor01 : theme.textColor03};
	flex-grow: 0;
	flex-shrink: 0;

	&:hover {
		background-color: ${({ theme, buttonType = 'secondary' }) =>
			buttonType === 'secondary' ? theme.ui01Hover : theme.ui03Hover};
	}

	&:focus {
		box-shadow: 0 0 0 2px ${({ theme }) => theme.focusColor};
	}

	&:disabled {
		cursor: not-allowed;
		background-color: grey;
		color: ${({ theme }) => theme.textColor01};
	}
`;

export default Button;
