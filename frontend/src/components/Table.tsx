import React from 'react';
import { VariableSizeList } from 'react-window';
import TableHeader from './TableHeader';
import TableBody from './TableBody';
import { Pokemon } from '../types/pokemon';

interface TableProps {
  filteredPokemon: Pokemon[];
  isMobile: boolean;
  windowHeight: number;
  listRef: React.RefObject<VariableSizeList | null>;
  rowHeights: React.MutableRefObject<Record<number, number>>;
  setRowHeight: (index: number, size: number) => void;
}

const Table: React.FC<TableProps> = ({
  filteredPokemon,
  isMobile,
  windowHeight,
  listRef,
  rowHeights,
  setRowHeight
}) => {
  return (
    <div role="table" aria-label="Pokemon data table" className="flex-1 flex flex-col overflow-hidden">
      <TableHeader isMobile={isMobile} />
      <TableBody
        filteredPokemon={filteredPokemon}
        isMobile={isMobile}
        windowHeight={windowHeight}
        listRef={listRef}
        rowHeights={rowHeights}
        setRowHeight={setRowHeight}
      />
    </div>
  );
};

export default Table;
