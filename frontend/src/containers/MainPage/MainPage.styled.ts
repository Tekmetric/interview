import styled from 'styled-components';

export const MainContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  flex-direction: column;
`;

export const Header = styled.div`
  height: 120px;
  width: 80%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: ${({ theme }) => theme.colors.background};
  opacity: ${({ theme }) => theme.opacity.background};
  margin: ${({ theme }) => theme.spacing.m};
`;
