import styled from 'styled-components';

const Container = styled.div`
	display: grid;
	grid-template-rows: auto 1fr;
	height: 100%;
	width: 100%;
	overflow: hidden;
`;

const ContentWrapper = styled.div`
	display: flex;
	flex-direction: column;
	padding: ${({ theme }) => theme.spacing04};
	width: 100%;
	height: 100%;
	gap: ${({ theme }) => theme.spacing03};
	background-color: ${({ theme }) => theme.ui01};
	color: ${({ theme }) => theme.textColor01};
	overflow: hidden;
`;

export { Container, ContentWrapper };
