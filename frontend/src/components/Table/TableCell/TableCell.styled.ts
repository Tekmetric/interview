import styled from 'styled-components';
import { ROW_HEIGHT } from '../TableRow/TableRow.styled';

interface TableCellContainerProps {
  $width?: number;
}

export const TableCellContainer = styled.div<TableCellContainerProps>`
  height: ${ROW_HEIGHT}px;
  width: ${({ $width }) => ($width ? `${$width}%` : `${(3 / 9) * 100}%`)};
  display: flex;
  align-items: center;
`;
