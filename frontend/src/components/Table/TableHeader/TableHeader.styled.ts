import styled from 'styled-components';

export const TableHeaderContainer = styled.div`
  background: ${({ theme }) => theme.colors.headerColor};
  color: ${({ theme }) => theme.colors.headerText};
  font-size: ${({ theme }) => theme.fontSize.m};
  text-align: left;
  padding-left: ${({ theme }) => theme.spacing.s};
  padding-right: ${({ theme }) => theme.spacing.m};
  display: flex;
  flex-direction: row;
  align-items: center;
`;
