import React, { useCallback, useMemo } from 'react';

import { PaginationInfo } from '../../types';

interface PaginationProps {
  pagination: PaginationInfo;
  onPageChange: (page: number) => void;
  onPageSizeChange: (pageSize: number) => void;
}

export const Pagination: React.FC<PaginationProps> = React.memo(
  ({ pagination, onPageChange, onPageSizeChange }) => {
    const { currentPage, totalPages, pageSize, totalRecords } = pagination;

    const pageNumbers = useMemo(() => {
      const pages: (number | string)[] = [];
      const maxVisiblePages = 5;

      if (totalPages <= maxVisiblePages) {
        for (let i = 1; i <= totalPages; i++) {
          pages.push(i);
        }
      } else {
        if (currentPage <= 3) {
          pages.push(1, 2, 3, 4, '...', totalPages);
        } else if (currentPage >= totalPages - 2) {
          pages.push(1, '...', totalPages - 3, totalPages - 2, totalPages - 1, totalPages);
        } else {
          pages.push(1, '...', currentPage - 1, currentPage, currentPage + 1, '...', totalPages);
        }
      }

      return pages;
    }, [currentPage, totalPages]);

    const handlePageSizeChange = useCallback(
      (e: React.ChangeEvent<HTMLSelectElement>) => {
        onPageSizeChange(parseInt(e.target.value));
      },
      [onPageSizeChange]
    );

    const startRecord = (currentPage - 1) * pageSize + 1;
    const endRecord = Math.min(currentPage * pageSize, totalRecords);

    if (totalPages === 0) {
      return null;
    }

    return (
      <div className='flex flex-col gap-4 rounded-lg bg-white p-6 shadow transition-colors duration-200 dark:bg-gray-800 dark:shadow-gray-900/20 sm:flex-row sm:items-center sm:justify-between'>
        {/* Results Info */}
        <div className='text-sm text-gray-700 dark:text-gray-300'>
          Showing <span className='font-medium'>{startRecord}</span> to{' '}
          <span className='font-medium'>{endRecord}</span> of{' '}
          <span className='font-medium'>{totalRecords}</span> results
        </div>

        <div className='flex flex-col gap-4 sm:flex-row sm:items-center'>
          {/* Page Size Selector */}
          <div className='flex items-center gap-2'>
            <label htmlFor='pageSize' className='text-sm text-gray-700 dark:text-gray-300'>
              Show:
            </label>
            <select
              id='pageSize'
              value={pageSize}
              onChange={handlePageSizeChange}
              className='rounded-md border border-gray-300 bg-white px-3 py-1 text-sm transition-colors duration-200 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:border-gray-600 dark:bg-gray-700 dark:text-white'
            >
              <option value={5}>5</option>
              <option value={10}>10</option>
              <option value={20}>20</option>
              <option value={50}>50</option>
            </select>
            <span className='text-sm text-gray-700 dark:text-gray-300'>per page</span>
          </div>

          {/* Pagination Controls */}
          <nav className='flex items-center gap-1' aria-label='Pagination Navigation'>
            {/* Previous Button */}
            <button
              onClick={() => onPageChange(currentPage - 1)}
              disabled={currentPage === 1}
              className='btn-outline text-sm'
              aria-label='Go to previous page'
            >
              Previous
            </button>

            {/* Page Numbers */}
            {pageNumbers.map((page, index) => (
              <React.Fragment key={`page-${index}`}>
                {page === '...' ? (
                  <span
                    className='px-3 py-2 text-sm text-gray-500 dark:text-gray-400'
                    aria-hidden='true'
                  >
                    …
                  </span>
                ) : (
                  <button
                    onClick={() => onPageChange(page as number)}
                    className={`rounded-md border px-3 py-2 text-sm font-medium transition-colors duration-200 ${
                      currentPage === page
                        ? 'border-blue-500 bg-blue-600 text-white'
                        : 'border-gray-300 bg-white text-gray-500 hover:bg-gray-50 dark:border-gray-600 dark:bg-gray-700 dark:text-gray-300 dark:hover:bg-gray-600'
                    }`}
                    aria-label={`Go to page ${page}`}
                    aria-current={currentPage === page ? 'page' : undefined}
                  >
                    {page}
                  </button>
                )}
              </React.Fragment>
            ))}

            {/* Next Button */}
            <button
              onClick={() => onPageChange(currentPage + 1)}
              disabled={currentPage === totalPages}
              className='btn-outline text-sm'
              aria-label='Go to next page'
            >
              Next
            </button>
          </nav>
        </div>
      </div>
    );
  }
);

Pagination.displayName = 'Pagination';
