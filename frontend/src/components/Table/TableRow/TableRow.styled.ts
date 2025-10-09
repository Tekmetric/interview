import styled from 'styled-components';

export const ROW_HEIGHT = 36;

export const TableRowContainer = styled.div`
  display: flex;
  flex-direction: row;

  height: ${`${ROW_HEIGHT}px`};
  padding-left: ${({ theme }) => theme.spacing.s};
  
  &:hover {
    background: ${({ theme }) => theme.colors.hover};
    cursor: pointer;
  }

  &:active {
    background: ${({ theme }) => theme.colors.active};
    cursor: pointer;
  }
`;
