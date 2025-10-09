import { AdditionalDetails, AnimeRow } from '../../types';

export type HeaderCell = {
  label: string;
  key: string;
  isSortable: boolean;
  sortDirection?: 'asc' | 'desc';
  size: number;
};

export type ActiveSorting = {
  columnId: string;
  sortDirection: string;
};

export type TableProps = {
  rows: AnimeRow[];
  fetchNextPage: () => {};
  isFetching: boolean;
  headers: HeaderCell[];
  activeSorting: ActiveSorting;
  onSort: (_columnId: string, _sortDirection: string) => void;
  hasError?: boolean
};

export type TableBodyRowProps = {
  row: AnimeRow;
};

export type TableBodyProps = Pick<TableProps, 'rows' | 'fetchNextPage' | 'isFetching'>;

export type TableHeaderCellProps = Pick<TableProps, 'onSort' | 'activeSorting'> & {
  id: string;
  value: string;
  width?: number;
  isSortable: boolean;
};

export type TableHeaderProps = Pick<TableProps, 'headers' | 'activeSorting' | 'onSort'>;

export type TabelCellValue = string | number | AdditionalDetails[];

export type TableCellProps = {
  value: TabelCellValue;
  type?: string;
  width?: number;
};
