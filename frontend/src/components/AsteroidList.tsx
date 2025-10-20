import { navigate } from 'astro:transitions/client';
import styles from './AsteroidList.module.css';
import type { NeoWsBrowseResponse } from '../schemas/nasa';

interface AsteroidListProps {
  page: number;
  data: NeoWsBrowseResponse;
}

export default function AsteroidList({ page, data }: AsteroidListProps) {

  console.log('AsteroidList data:', { page, data });

  // Navigation helper - uses Astro View Transitions
  const navigateToPage = (p: number) => {
    navigate(`/browse?page=${p}`);
  };

  const handlePrevious = () => {
    if (page > 0) {
      navigateToPage(page - 1);
    }
  };

  const handleNext = () => {
    if (data && page < data.page.total_pages - 1) {
      navigateToPage(page + 1);
    }
  };

  const handlePageClick = (p: number) => {
    navigateToPage(p);
  };

  if (!data || !data.near_earth_objects) {
    return null;
  }

  // Generate page numbers to display (show current, +/- 2 pages)
  const pageNumbers: number[] = [];
  const totalPages = data.page.total_pages;
  const startPage = Math.max(0, page - 2);
  const endPage = Math.min(totalPages - 1, page + 2);

  for (let i = startPage; i <= endPage; i++) {
    pageNumbers.push(i);
  }

  return (
    <div className={styles.container}>
      <div className={styles.pageInfo}>
        <p>
          Page {page + 1} of {data.page.total_pages}
        </p>
        <p className={styles.totalCount}>
          Total asteroids: {data.page.total_elements.toLocaleString()}
        </p>
      </div>

      <div className={styles.pagination}>
        <button
          onClick={handlePrevious}
          disabled={page === 0}
          className={styles.pageButton}
        >
          Previous
        </button>

        {startPage > 0 && (
          <>
            <button
              onClick={() => handlePageClick(0)}
              className={styles.pageNumber}
            >
              1
            </button>
            {startPage > 1 && <span className={styles.ellipsis}>...</span>}
          </>
        )}

        {pageNumbers.map((p) => (
          <button
            key={p}
            onClick={() => handlePageClick(p)}
            className={`${styles.pageNumber} ${
              p === page ? styles.active : ''
            }`}
          >
            {p + 1}
          </button>
        ))}

        {endPage < totalPages - 1 && (
          <>
            {endPage < totalPages - 2 && <span className={styles.ellipsis}>...</span>}
            <button
              onClick={() => handlePageClick(totalPages - 1)}
              className={styles.pageNumber}
            >
              {totalPages}
            </button>
          </>
        )}

        <button
          onClick={handleNext}
          disabled={page >= data.page.total_pages - 1}
          className={styles.pageButton}
        >
          Next
        </button>
      </div>

      <div className={styles.asteroidList}>
        {data.near_earth_objects.map((asteroid) => (
          <div key={asteroid.id} className={styles.asteroidCard}>
            <div className={styles.asteroidHeader}>
              <h3 className={styles.asteroidName}>{asteroid.name}</h3>
              {asteroid.is_potentially_hazardous_asteroid && (
                <span className={styles.hazardBadge}>Potentially Hazardous</span>
              )}
            </div>
            <div className={styles.asteroidDetails}>
              <p>
                <strong>ID:</strong> {asteroid.id}
              </p>
              <p>
                <strong>Absolute Magnitude:</strong>{' '}
                {asteroid.absolute_magnitude_h.toFixed(2)}
              </p>
              <p>
                <strong>Estimated Diameter:</strong>{' '}
                {asteroid.estimated_diameter.kilometers.estimated_diameter_min.toFixed(
                  3
                )}{' '}
                -{' '}
                {asteroid.estimated_diameter.kilometers.estimated_diameter_max.toFixed(
                  3
                )}{' '}
                km
              </p>
            </div>
            <a href={`/asteroid/${asteroid.id}`} className={styles.detailsLink}>
              View Details →
            </a>
          </div>
        ))}
      </div>

      <div className={styles.pagination}>
        <button
          onClick={handlePrevious}
          disabled={page === 0}
          className={styles.pageButton}
        >
          Previous
        </button>

        {startPage > 0 && (
          <>
            <button
              onClick={() => handlePageClick(0)}
              className={styles.pageNumber}
            >
              1
            </button>
            {startPage > 1 && <span className={styles.ellipsis}>...</span>}
          </>
        )}

        {pageNumbers.map((p) => (
          <button
            key={p}
            onClick={() => handlePageClick(p)}
            className={`${styles.pageNumber} ${
              p === page ? styles.active : ''
            }`}
          >
            {p + 1}
          </button>
        ))}

        {endPage < totalPages - 1 && (
          <>
            {endPage < totalPages - 2 && <span className={styles.ellipsis}>...</span>}
            <button
              onClick={() => handlePageClick(totalPages - 1)}
              className={styles.pageNumber}
            >
              {totalPages}
            </button>
          </>
        )}

        <button
          onClick={handleNext}
          disabled={page >= data.page.total_pages - 1}
          className={styles.pageButton}
        >
          Next
        </button>
      </div>
    </div>
  );
}
