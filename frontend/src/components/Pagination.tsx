import { navigate } from 'astro:transitions/client';
import styles from './Pagination.module.css';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
}

export default function Pagination({ currentPage, totalPages }: PaginationProps) {
  const handlePrevious = () => {
    if (currentPage > 0) {
      navigate(`/browse?page=${currentPage - 1}`);
    }
  };

  const handleNext = () => {
    if (currentPage < totalPages - 1) {
      navigate(`/browse?page=${currentPage + 1}`);
    }
  };

  // Generate page numbers to display (show current, +/- 2 pages)
  const pageNumbers: number[] = [];
  const startPage = Math.max(0, currentPage - 2);
  const endPage = Math.min(totalPages - 1, currentPage + 2);

  for (let i = startPage; i <= endPage; i++) {
    pageNumbers.push(i);
  }

  return (
    <div className={styles.pagination}>
      <button
        onClick={handlePrevious}
        disabled={currentPage === 0}
        className={styles.pageButton}
      >
        Previous
      </button>

      {startPage > 0 && (
        <>
          <a
            href="/browse?page=0"
            className={styles.pageNumber}
          >
            1
          </a>
          {startPage > 1 && <span className={styles.ellipsis}>...</span>}
        </>
      )}

      {pageNumbers.map((p) => (
        <a
          key={p}
          href={`/browse?page=${p}`}
          className={`${styles.pageNumber} ${
            p === currentPage ? styles.active : ''
          }`}
        >
          {p + 1}
        </a>
      ))}

      {endPage < totalPages - 1 && (
        <>
          {endPage < totalPages - 2 && <span className={styles.ellipsis}>...</span>}
          <a
            href={`/browse?page=${totalPages - 1}`}
            className={styles.pageNumber}
          >
            {totalPages}
          </a>
        </>
      )}

      <button
        onClick={handleNext}
        disabled={currentPage >= totalPages - 1}
        className={styles.pageButton}
      >
        Next
      </button>
    </div>
  );
}
