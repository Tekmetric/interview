import { Button } from '../button/Button';
import { getVisiblePages } from './paginationUtils';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

export function Pagination({
  currentPage,
  totalPages,
  onPageChange,
}: PaginationProps) {
  if (totalPages <= 1) {
    return null;
  }

  const visiblePages = getVisiblePages(currentPage, totalPages);

  return (
    <nav
      className="mt-6 flex flex-wrap items-center justify-center gap-2"
      aria-label="Product pagination"
    >
      <Button
        variant="secondary"
        aria-label="Previous page"
        disabled={currentPage === 1}
        onClick={() => onPageChange(currentPage - 1)}
      >
        Previous
      </Button>

      {visiblePages.map((item, index) =>
        item === 'ellipsis' ? (
          <span
            key={`ellipsis-${index}`}
            className="px-1 text-sm text-neutral-500"
            aria-hidden="true"
          >
            …
          </span>
        ) : (
          <Button
            key={item}
            variant={item === currentPage ? 'primary' : 'secondary'}
            aria-label={`Page ${item}`}
            aria-current={item === currentPage ? 'page' : undefined}
            onClick={() => onPageChange(item)}
          >
            {item}
          </Button>
        )
      )}

      <Button
        variant="secondary"
        aria-label="Next page"
        disabled={currentPage === totalPages}
        onClick={() => onPageChange(currentPage + 1)}
      >
        Next
      </Button>
    </nav>
  );
}
