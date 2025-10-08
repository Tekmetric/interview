import styled from 'styled-components';

export const TableHeaderContainer = styled.div`
  background: ${({ theme }) => theme.colors.headerColor};
  color: white;
  height: 48px;
  font-size: ${({ theme }) => theme.fontSize.m};
  display: block;
  text-align: left;
  padding-left: ${({ theme }) => theme.spacing.s};
  padding-right: ${({ theme }) => theme.spacing.m};
  display: flex;
  flex-direction: row;
  align-items: center;
`;
