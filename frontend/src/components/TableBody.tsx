import React, { useRef, useEffect, Suspense, lazy } from 'react';
import { useTranslation } from 'react-i18next';
import { VariableSizeList, ListChildComponentProps } from 'react-window';
import { useAppSelector } from '../store/hooks';
import { convertHeight, convertWeight, capitalize } from '../lib/utils';
import { COLUMN_WIDTHS, typeColors, classes } from '../lib/styles';
import { TableCell } from './TableCell';
import { KeyboardNavigationWrapper } from './KeyboardNavigation';
import { Pokemon } from '../types/pokemon';

const BarChart = lazy(() => import('./BarChart'));

const Cell = TableCell;

interface TableBodyProps {
  filteredPokemon: Pokemon[];
  isMobile: boolean;
  windowHeight: number;
  listRef: React.RefObject<VariableSizeList | null>;
  rowHeights: React.MutableRefObject<Record<number, number>>;
  setRowHeight: (index: number, size: number) => void;
}

const TableBody: React.FC<TableBodyProps> = ({
  filteredPokemon,
  isMobile,
  windowHeight,
  listRef,
  rowHeights,
  setRowHeight
}) => {
  const { t } = useTranslation();
  const isMetric = useAppSelector((state) => state.pokemon.isMetric);

  const getRowHeight = (index: number): number => {
    return rowHeights.current[index] || 120;
  };

  const Row: React.FC<ListChildComponentProps> = ({ index, style }) => {
    const pokemon = filteredPokemon[index];
    const rowRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
      if (rowRef.current) {
        setRowHeight(index, rowRef.current.getBoundingClientRect().height);
      }
    }, [index]);

    if (!pokemon) {
      return null;
    }

    const height = convertHeight(pokemon.height, isMetric);
    const weight = convertWeight(pokemon.weight, isMetric);

    return (
      <div
        ref={rowRef}
        style={style}
        className={classes.row}
        role="row"
        aria-label={`pokemon: ${capitalize(pokemon.name)}, Number ${pokemon.id}`}
      >
        <Cell width={COLUMN_WIDTHS.id} className={classes.cellId(isMobile)}>
          {pokemon.id}
        </Cell>
        <Cell width={COLUMN_WIDTHS.image} className={classes.cellImage(isMobile)}>
          <img
            src={pokemon.sprites.front_default || ''}
            alt={`${capitalize(pokemon.name)} sprite`}
            aria-label={`${capitalize(pokemon.name)} image`}
            className={classes.pokemonImage}
          />
          <div className={classes.typeBadgeContainer}>
            {pokemon.types?.map((type) => (
              <span
                key={type.type.name}
                className={classes.typeBadge}
                style={{backgroundColor: typeColors[type.type.name] || '#A8A77A'}}
              >
                {type.type.name}
              </span>
            )) || 'Unknown'}
          </div>
        </Cell>
        <Cell width={COLUMN_WIDTHS.name} className={classes.cellName(isMobile)}>
          <a
            href={`https://www.pokemon.com/us/pokedex/${pokemon.name}`}
            target="_blank"
            rel="noopener noreferrer"
            className={classes.pokemonLink}
          >
            {capitalize(pokemon.name)}
          </a>
        </Cell>
        <Cell width={COLUMN_WIDTHS.height} className={classes.cellHeight(isMobile)}>
          {height}
        </Cell>
        <Cell width={COLUMN_WIDTHS.weight} className={classes.cellWeight(isMobile)}>
          {weight}
        </Cell>
        {!isMobile && (
          <Cell width={COLUMN_WIDTHS.stats} className={classes.cellStats}>
            <Suspense fallback={<div className="h-16 w-full flex items-center justify-center text-gray-400 text-sm">Loading chart...</div>}>
              <BarChart stats={pokemon.stats} />
            </Suspense>
          </Cell>
        )}
      </div>
    );
  };

  return (
    <KeyboardNavigationWrapper
      listRef={listRef}
      itemCount={filteredPokemon.length}
      className={classes.listContainer(isMobile)}
      ariaLabel={t('table.ariaLabel')}
    >
      <VariableSizeList
        ref={listRef}
        height={windowHeight - (isMobile ? 260 : 300)}
        itemCount={filteredPokemon.length}
        itemSize={getRowHeight}
        width="100%"
        style={{ overflowX: 'hidden' }}
      >
        {Row}
      </VariableSizeList>
    </KeyboardNavigationWrapper>
  );
};

export default TableBody;
