import styled from 'styled-components';

const Container = styled.div`
	display: flex;
	flex-direction: column;
	gap: ${({ theme }) => theme.spacing03};
`;

const SearchBox = styled.input``;

export { Container, SearchBox };
