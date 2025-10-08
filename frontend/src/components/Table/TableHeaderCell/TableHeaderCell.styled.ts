import styled from 'styled-components';

import { TableCellContainerProps } from '../TableCell/TableCell.styled';
import { ROW_HEIGHT } from '../TableRow/TableRow.styled';

export const TableHeaderCellContainer = styled.div<TableCellContainerProps & { $active?: boolean }>`
  height: ${ROW_HEIGHT}px;
  width: ${({ $width }) => ($width ? `${$width}%` : `${(3 / 9) * 100}%`)};
  display: flex;
  align-items: center;
  color: ${({ $active, theme }) => ($active && theme.colors.headerActiveText)};
  font-weight: ${({ $active }) => ($active && 700)};

  &:hover {
    cursor: pointer;
  }
`;
