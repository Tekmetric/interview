import styled from 'styled-components';

import { ContainerStyles } from '../../shared/styledComponents';

export const MainContainer = styled.div`
  ${ContainerStyles}
`;

export const Header = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  height: 120px;
  width: 80%;
  
  margin-top: ${({ theme }) => theme.spacing.l};
  margin-bottom: ${({ theme }) => theme.spacing.s};

  background-color: ${({ theme }) => theme.colors.background};
  opacity: ${({ theme }) => theme.opacity.background};
  
  font-size: ${({ theme }) => theme.fontSize.m};
`;

export const Button = styled.button`
  height: 30px;

  margin-top: ${({ theme }) => theme.spacing.s};
  background-color: ${({ theme }) => theme.colors.buttonColor};
  border: 0;
  border-radius: 4px;

  &:hover {
    cursor: pointer;
    background-color: ${({ theme }) => theme.colors.hover};
  }

   &:active {
    background-color: ${({ theme }) => theme.colors.active};
  }
`;
