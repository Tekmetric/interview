import { useWindowVirtualizer } from '@tanstack/react-virtual';
import { useLayoutEffect, useRef, useState } from 'react';
import styled from 'styled-components';

import type { Character } from '../../api/types';
import { CharacterCard } from './CharacterCard';

// Card min-width (220px) plus the grid gap.
const COLUMN_WIDTH = 236;

const Viewport = styled.div`
  position: relative;
  width: 100%;
`;

const Row = styled.div<{ $columns: number }>`
  position: absolute;
  top: 0;
  left: 0;
  display: grid;
  grid-template-columns: repeat(${({ $columns }) => $columns}, 1fr);
  gap: ${({ theme }) => theme.space.md};
  width: 100%;
  padding-block-end: ${({ theme }) => theme.space.md};
`;

// Only the rows near the viewport exist in the DOM: with all 826 characters
// loaded the node count stays flat instead of growing per page. Rows scroll
// with the window (no inner scrollbar), and each row measures itself so text
// wrapping can vary per row.
export function VirtualizedCharacterGrid({ characters }: { characters: Character[] }) {
  const viewportRef = useRef<HTMLDivElement>(null);
  const [columnCount, setColumnCount] = useState(1);
  // The list's distance from the top of the page, so window scroll positions
  // translate to row positions. Measured in an effect: refs must not be read
  // during render (react-hooks/refs).
  const [scrollMargin, setScrollMargin] = useState(0);

  useLayoutEffect(() => {
    const viewport = viewportRef.current;
    if (!viewport) {
      return;
    }
    const measure = () => {
      setColumnCount(Math.max(1, Math.floor(viewport.clientWidth / COLUMN_WIDTH)));
      setScrollMargin(viewport.offsetTop);
    };
    measure();
    const observer = new ResizeObserver(measure);
    observer.observe(viewport);
    return () => observer.disconnect();
  }, []);

  const rowCount = Math.ceil(characters.length / columnCount);
  const virtualizer = useWindowVirtualizer({
    count: rowCount,
    estimateSize: () => 360,
    overscan: 2,
    scrollMargin,
  });

  return (
    <Viewport ref={viewportRef} role="list" style={{ height: virtualizer.getTotalSize() }}>
      {virtualizer.getVirtualItems().map((virtualRow) => {
        const rowCharacters = characters.slice(
          virtualRow.index * columnCount,
          (virtualRow.index + 1) * columnCount,
        );
        return (
          <Row
            key={virtualRow.key}
            ref={virtualizer.measureElement}
            data-index={virtualRow.index}
            $columns={columnCount}
            style={{
              transform: `translateY(${virtualRow.start - virtualizer.options.scrollMargin}px)`,
            }}
          >
            {rowCharacters.map((character) => (
              <div role="listitem" key={character.id}>
                <CharacterCard character={character} />
              </div>
            ))}
          </Row>
        );
      })}
    </Viewport>
  );
}
