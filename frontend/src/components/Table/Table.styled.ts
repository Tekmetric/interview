import styled from 'styled-components';

export const TableContainer = styled.div`
  display: flex;
  flex-direction: column;

  height: 80%;
  width: 80%;
  border-spacing: 0;

  background: ${({ theme }) => theme.colors.background};;
  opacity: ${({ theme }) => theme.opacity.background};
  
`;
