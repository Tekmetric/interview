import styled from 'styled-components';

export const ROW_HEIGHT = 36;

export const TableRowContainer = styled.div`
  height: ${`${ROW_HEIGHT}px`};
  display: flex;
  flex-direction: row;
  padding-left: 10px;
  
  &:hover {
    background: #E0D5D9;
    cursor: pointer;
  }

  &:active {
    background: #705E84;
    cursor: pointer;
  }
`;
