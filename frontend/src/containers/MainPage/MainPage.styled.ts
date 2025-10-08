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
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: ${({ theme }) => theme.colors.background};
  opacity: ${({ theme }) => theme.opacity.background};
  margin-top: ${({ theme }) => theme.spacing.l};
  margin-bottom: ${({ theme }) => theme.spacing.s};
  font-size: ${({ theme }) => theme.fontSize.m};
`;

export const Button = styled.button`
  margin-top: ${({ theme }) => theme.spacing.s};
  background-color: ${({ theme }) => theme.colors.buttonColor};
  border: 0;
  border-radius: 4px;
  height: 30px;

  &:hover {
    cursor: pointer;
    background-color: ${({ theme }) => theme.colors.hover};
  }

   &:active {
    background-color: ${({ theme }) => theme.colors.active};
  }
`;
