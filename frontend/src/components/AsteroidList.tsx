import { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import styles from './AsteroidList.module.css';
import type { NeoWsBrowseResponse } from '../schemas/nasa';

export default function AsteroidList() {
  const [currentPage, setCurrentPage] = useState(0);

  // Initialize page from URL on mount
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const pageParam = params.get('page');
    if (pageParam) {
      setCurrentPage(parseInt(pageParam, 10));
    }
  }, []);

  // Update URL when page changes
  useEffect(() => {
    const url = new URL(window.location.href);
    url.searchParams.set('page', currentPage.toString());
    window.history.pushState({}, '', url);
  }, [currentPage]);

  // Fetch data with React Query
  const { data, isLoading, error } = useQuery<NeoWsBrowseResponse, Error>({
    queryKey: ['asteroids', 'browse', currentPage],
    queryFn: async () => {
      const response = await fetch(`/api/neows?page=${currentPage}`);

      if (!response.ok) {
        throw new Error('Failed to fetch asteroid data');
      }

      const responseData = await response.json();

      if (responseData.error) {
        throw new Error(responseData.error);
      }

      return responseData;
    },
  });

  const handlePrevious = () => {
    if (currentPage > 0) {
      setCurrentPage(currentPage - 1);
    }
  };

  const handleNext = () => {
    if (data && currentPage < data.page.total_pages - 1) {
      setCurrentPage(currentPage + 1);
    }
  };

  const handlePageClick = (page: number) => {
    setCurrentPage(page);
  };

  if (isLoading) {
    return (
      <div className={styles.loading}>
        <p>Loading asteroids...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className={styles.error}>
        <p>Error: {error.message}</p>
      </div>
    );
  }

  if (!data || !data.near_earth_objects) {
    return null;
  }

  // Generate page numbers to display (show current, +/- 2 pages)
  const pageNumbers: number[] = [];
  const totalPages = data.page.total_pages;
  const startPage = Math.max(0, currentPage - 2);
  const endPage = Math.min(totalPages - 1, currentPage + 2);

  for (let i = startPage; i <= endPage; i++) {
    pageNumbers.push(i);
  }

  return (
    <div className={styles.container}>
      <div className={styles.pageInfo}>
        <p>
          Page {currentPage + 1} of {data.page.total_pages}
        </p>
        <p className={styles.totalCount}>
          Total asteroids: {data.page.total_elements.toLocaleString()}
        </p>
      </div>

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
            <button
              onClick={() => handlePageClick(0)}
              className={styles.pageNumber}
            >
              1
            </button>
            {startPage > 1 && <span className={styles.ellipsis}>...</span>}
          </>
        )}

        {pageNumbers.map((page) => (
          <button
            key={page}
            onClick={() => handlePageClick(page)}
            className={`${styles.pageNumber} ${
              page === currentPage ? styles.active : ''
            }`}
          >
            {page + 1}
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
          disabled={currentPage >= data.page.total_pages - 1}
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
          disabled={currentPage === 0}
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

        {pageNumbers.map((page) => (
          <button
            key={page}
            onClick={() => handlePageClick(page)}
            className={`${styles.pageNumber} ${
              page === currentPage ? styles.active : ''
            }`}
          >
            {page + 1}
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
          disabled={currentPage >= data.page.total_pages - 1}
          className={styles.pageButton}
        >
          Next
        </button>
      </div>
    </div>
  );
}
