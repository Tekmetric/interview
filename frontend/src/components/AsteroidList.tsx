import styles from './AsteroidList.module.css';
import type { NeoWsBrowseResponse } from '../schemas/nasa';
import Pagination from './Pagination';

interface AsteroidListProps {
  page: number;
  data: NeoWsBrowseResponse;
}

export default function AsteroidList({ page, data }: AsteroidListProps) {
  if (!data || !data.near_earth_objects) {
    return null;
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

      <Pagination currentPage={page} totalPages={data.page.total_pages} />

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

      <Pagination currentPage={page} totalPages={data.page.total_pages} />
    </div>
  );
}
