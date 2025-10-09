import styled from 'styled-components';

export const TableBodyContainer = styled.div`
  display: block;

  height: 100%;
  width: 100%;

  vertical-align: text-top;
  overflow: hidden;
`;

export const TableBodyScroll = styled.div`
  height: 100%;
  
  overflow-y: scroll;
  scrollbar-color: ${({ theme }) => `${theme.colors.scrollbarColor} ${theme.colors.scrollbarBackGdColor}`};
`;
