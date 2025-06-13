import styled from 'styled-components';

const Container = styled.div`
	display: flex;
	flex-direction: column;
	gap: ${({ theme }) => theme.spacing03};
`;

const SearchBoxContainer = styled.div`
	display: flex;
	align-items: center;
	gap: ${({ theme }) => theme.spacing03};
`;
const SearchBox = styled.input`
	padding: ${({ theme }) => theme.spacing03} ${({ theme }) => theme.spacing04};
	width: 300px;
	max-width: 100%;
	border: 2px solid ${({ theme }) => theme.borderColor};
	border-radius: ${({ theme }) => theme.spacing02};

	&:focus {
		outline: none;
		border-color: ${({ theme }) => theme.focusColor};
	}
`;

const SecondaryToolbar = styled.div`
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: ${({ theme }) => theme.spacing03} ${({ theme }) => theme.spacing04};
`;

export { Container, SearchBoxContainer, SearchBox, SecondaryToolbar };
