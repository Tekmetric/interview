import React, { useRef, useEffect, Suspense, lazy } from 'react';
import { useTranslation } from 'react-i18next';
import { VariableSizeList, ListChildComponentProps } from 'react-window';
import { useAppSelector } from '../../store/hooks';
import { convertHeight, convertWeight, capitalize } from '../../lib/utils';
import { COLUMN_WIDTHS, typeColors, classes } from '../../lib/styles';
import { TableCell } from './TableCell';
import { KeyboardNavigationWrapper } from '../ui/KeyboardNavigation/KeyboardNavigation';
import { Pokemon } from '../../types/pokemon';
import { usePokemonPrefetch } from '../../store/api';

const BarChart = lazy(() => import('../ui/BarChart/BarChart'));

const Cell = TableCell;

const DEFAULT_ROW_HEIGHT = 120;

interface TableBodyProps {
  filteredPokemon: Pokemon[];
  isMobile: boolean;
  windowHeight: number;
  listRef: React.RefObject<VariableSizeList | null>;
  rowHeights: React.MutableRefObject<Record<number, number>>;
  setRowHeight: (index: number, size: number) => void;
}

/**
 * Transform Pokemon data for display
 */
interface PokemonDisplayData {
  id: number;
  name: string;
  displayName: string;
  height: string;
  weight: string;
  sprite: string;
  types: Pokemon['types'];
  stats: Pokemon['stats'];
}

const transformPokemonForDisplay = (pokemon: Pokemon, isMetric: boolean): PokemonDisplayData => ({
  id: pokemon.id,
  name: pokemon.name,
  displayName: capitalize(pokemon.name),
  height: convertHeight(pokemon.height, isMetric),
  weight: convertWeight(pokemon.weight, isMetric),
  sprite: pokemon.sprites.front_default || '',
  types: pokemon.types,
  stats: pokemon.stats,
});

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

  // Prefetch hook for optimistic data loading
  const { prefetchPokemon } = usePokemonPrefetch();

  const getRowHeight = (index: number): number => {
    return rowHeights.current[index] || DEFAULT_ROW_HEIGHT;
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

    const displayData = transformPokemonForDisplay(pokemon, isMetric);

    return (
      <div
        ref={rowRef}
        style={style}
        className={classes.row}
        role="row"
        aria-label={`pokemon: ${displayData.displayName}, Number ${displayData.id}`}
        onMouseEnter={() => prefetchPokemon(displayData.id)}
      >
        <Cell width={COLUMN_WIDTHS.id} className={classes.cellId(isMobile)}>
          {displayData.id}
        </Cell>
        <Cell width={COLUMN_WIDTHS.image} className={classes.cellImage(isMobile)}>
          <img
            src={displayData.sprite}
            alt={`${displayData.displayName} sprite`}
            aria-label={`${displayData.displayName} image`}
            className={classes.pokemonImage}
          />
          <div className={classes.typeBadgeContainer}>
            {displayData.types?.map((type) => (
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
            href={`https://www.pokemon.com/us/pokedex/${displayData.name}`}
            target="_blank"
            rel="noopener noreferrer"
            className={classes.pokemonLink}
          >
            {displayData.displayName}
          </a>
        </Cell>
        <Cell width={COLUMN_WIDTHS.height} className={classes.cellHeight(isMobile)}>
          {displayData.height}
        </Cell>
        <Cell width={COLUMN_WIDTHS.weight} className={classes.cellWeight(isMobile)}>
          {displayData.weight}
        </Cell>
        {!isMobile && (
          <Cell width={COLUMN_WIDTHS.stats} className={classes.cellStats}>
            <Suspense fallback={<div className="h-16 w-full flex items-center justify-center text-gray-400 text-sm">Loading chart...</div>}>
              <BarChart stats={displayData.stats} />
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
