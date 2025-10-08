import styled from 'styled-components';

export const TableRowContainer = styled.div`
  height: 36px;
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
