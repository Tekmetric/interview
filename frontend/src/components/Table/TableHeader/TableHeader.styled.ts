import styled from 'styled-components';

export const TableHeaderContainer = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;

  padding-left: ${({ theme }) => theme.spacing.s};
  padding-right: ${({ theme }) => theme.spacing.m};

  background: ${({ theme }) => theme.colors.headerColor};
  color: ${({ theme }) => theme.colors.headerText};
  font-size: ${({ theme }) => theme.fontSize.m};
  text-align: left;
  
`;
