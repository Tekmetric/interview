import styled from 'styled-components';

export const TableBodyContainer = styled.div`
  vertical-align: text-top;
  overflow: hidden;
  display: block;
  height: 100%;
  width: 100%;
`;

export const TableBodyScroll = styled.div`
  height: 100%;
  overflow-y: scroll;
  scrollbar-color: ${({ theme }) => `${theme.colors.scrollbarColor} ${theme.colors.scrollbarBackGdColor}`};
`;
