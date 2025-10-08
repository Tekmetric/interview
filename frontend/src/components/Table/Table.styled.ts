import styled from 'styled-components';

export const TableContainer = styled.div`
  height: 80%;
  width: 80%;
  background: white;
  opacity: ${({ theme }) => theme.opacity.background};
  border-spacing: 0;
  display: flex;
  flex-direction: column;
`;
