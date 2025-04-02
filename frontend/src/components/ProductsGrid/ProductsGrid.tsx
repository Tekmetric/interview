import React, { useEffect, useMemo, useRef, useState } from 'react';

import { ProductsGridSkeleton } from './ProductsSkeleton.tsx';
import ProductsNotFound from './ProductsNotFound.tsx';
import { Product } from '../../types/Product.ts';
import ProductItem from './ProductItem.tsx';
import Button from '../Button/Button.tsx';

import { areEqual, FixedSizeGrid } from 'react-window';
import AutoSizer from 'react-virtualized-auto-sizer';
import memoizeOne from 'memoize-one';
import {
  FetchNextPageOptions,
  InfiniteQueryObserverResult,
} from '@tanstack/react-query';

interface CellProps {
  columnIndex: number;
  rowIndex: number;
  style: React.CSSProperties;
  data: {
    products: Product[];
    columnCount: number;
  };
}

const Cell = React.memo(({ columnIndex, rowIndex, style, data }: CellProps) => {
  const { products, columnCount } = data;
  const index = rowIndex * columnCount + columnIndex;
  const product = useMemo(() => products?.[index], [products, index]);

  // Return empty cell for items out of range
  if (index >= products.length) {
    return <div style={style}></div>;
  }

  return (
    <div style={style} className="p-2">
      <ProductItem product={product} />
    </div>
  );
}, areEqual);

const itemKey = ({ columnIndex, rowIndex, data }: Omit<CellProps, 'style'>) => {
  const { products, columnCount } = data;
  const index = rowIndex * columnCount + columnIndex;
  const product = products?.[index];

  return product ? `product-${product?.id}` : `${columnIndex}-${rowIndex}`;
};

const createItemData = memoizeOne(
  (products: Product[], columnCount: number) => ({
    products,
    columnCount,
  })
);

interface ProductsGridProps {
  products: Product[];
  showLoadMore: boolean;
  isFetching: boolean;
  isLoading: boolean;
  fetchNextPage: (
    options?: FetchNextPageOptions
  ) => Promise<InfiniteQueryObserverResult>;
}

const ProductsGrid = ({
  products,
  showLoadMore,
  isFetching,
  isLoading,
  fetchNextPage,
}: ProductsGridProps) => {
  const gridOuterRef = useRef<FixedSizeGrid>(null);
  // Ref to track previous product count for auto-scrolling
  const prevProductCountRef = useRef<number>(0);
  const [newDataLoaded, setNewDataLoaded] = useState(false);
  // State to track window width to detect layout changes
  const [windowWidth, setWindowWidth] = useState(window.innerWidth);

  // Determine how many columns based on screen size (responsive)
  const getColumnCount = (width: number): number => {
    if (width < 640) return 1; // Mobile: full width
    if (width < 768) return 2; // Tablet: 2 columns
    return 4; // Desktop: 4 columns
  };

  const columnCount = getColumnCount(windowWidth);
  const rowCount = Math.ceil(products.length / columnCount);
  const rowHeight = 330;

  useEffect(() => {
    const handleResize = () => {
      setWindowWidth(window.innerWidth);
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const smoothScrollToRow = (rowIndex: number) => {
    if (!gridOuterRef.current) return;

    const gridScrollableContainer =
      gridOuterRef.current as any as HTMLDivElement;

    if (!gridScrollableContainer) return;

    const scrollPosition = rowIndex * rowHeight - rowHeight / 2; // subtract half of the rowHeight to keep in view part of the previous row

    gridScrollableContainer.scrollTo({
      top: scrollPosition,
      behavior: 'smooth',
    });
  };

  // Auto-scroll when new products are loaded
  useEffect(() => {
    if (newDataLoaded) {
      const columnCount = getColumnCount(windowWidth);
      const previousLastItemIndex = prevProductCountRef.current;
      // Determine which row to scroll to (one row past the previous last row)
      const targetRow = Math.ceil(previousLastItemIndex / columnCount);

      // Wait for the grid to update with new items
      setTimeout(() => {
        smoothScrollToRow(targetRow);
      }, 150);

      setNewDataLoaded(false);
    }
  }, [newDataLoaded, windowWidth]);

  const itemData = createItemData(products, columnCount);

  const loadMoreProducts = () => {
    // Store the current number of products before loading more
    prevProductCountRef.current = products.length;
    fetchNextPage().then(() => {
      setNewDataLoaded(true);
    });
  };

  if (isLoading) {
    return <ProductsGridSkeleton />;
  }
  if (!products.length) {
    return <ProductsNotFound />;
  }

  return (
    <>
      <div className="flex-grow">
        <AutoSizer>
          {({ height, width }) => {
            return (
              <FixedSizeGrid
                outerRef={gridOuterRef}
                columnCount={columnCount}
                columnWidth={width / columnCount}
                height={height}
                rowCount={rowCount}
                rowHeight={rowHeight}
                width={width}
                itemData={itemData}
                itemKey={itemKey}
              >
                {Cell}
              </FixedSizeGrid>
            );
          }}
        </AutoSizer>
      </div>
      {showLoadMore && (
        <div className="flex justify-center">
          <Button
            onClick={loadMoreProducts}
            disabled={isFetching}
            isLoading={isFetching}
          >
            {isFetching ? 'Loading Products...' : 'Load More Products'}
          </Button>
        </div>
      )}
    </>
  );
};

export default ProductsGrid;
