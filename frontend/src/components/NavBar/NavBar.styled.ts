import styled from 'styled-components';

const Container = styled.div`
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: ${({ theme }) => theme.spacing03} ${({ theme }) => theme.spacing04};
	width: 100%;
	background-color: ${({ theme }) => theme.ui01};
	color: ${({ theme }) => theme.textColor01};
	box-shadow: 0 0 10px
		rgba(0, 0, 0, ${({ theme }) => (theme.themeName === 'light' ? 0.1 : 0.2)});
	position: sticky;
	top: 0;
	z-index: 1000;
`;

const Title = styled.div`
	${({ theme }) => theme.titleTypography};
`;

const ButtonsContainer = styled.div`
	display: flex;
	gap: ${({ theme }) => theme.spacing03};
`;

export { Container, Title, ButtonsContainer };
