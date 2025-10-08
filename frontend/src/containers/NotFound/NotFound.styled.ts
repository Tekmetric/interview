import styled from 'styled-components';

export const NotFoundContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: ${({ theme }) => theme.colors.background};
  opacity: ${({ theme }) => theme.opacity.backgroundLight};
  border-spacing: 0;
  display: flex;
  flex-direction: column;
`;
