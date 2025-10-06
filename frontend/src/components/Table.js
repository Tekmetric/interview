import React from 'react';
import PropTypes from 'prop-types';
import TableHeader from './TableHeader';
import TableBody from './TableBody';

const Table = ({ filteredPokemon, isMobile, windowHeight, listRef, rowHeights, setRowHeight }) => {
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

Table.propTypes = {
  filteredPokemon: PropTypes.arrayOf(PropTypes.object).isRequired,
  isMobile: PropTypes.bool.isRequired,
  windowHeight: PropTypes.number.isRequired,
  listRef: PropTypes.object.isRequired,
  rowHeights: PropTypes.object.isRequired,
  setRowHeight: PropTypes.func.isRequired,
};

export default Table;
