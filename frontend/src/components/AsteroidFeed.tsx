import { navigate } from 'astro:transitions/client';
import styles from './AsteroidFeed.module.css';
import type { NeoWsFeedResponse } from '../schemas/nasa';

interface AsteroidFeedProps {
  data: NeoWsFeedResponse;
  selectedDate: string;
}

export default function AsteroidFeed({ data, selectedDate }: AsteroidFeedProps) {
  const handleDateChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newDate = event.target.value;
    if (newDate) {
      navigate(`/?date=${newDate}`);
    }
  };

  // Format date as human-readable (e.g., "October 20, 2025")
  const formatDateReadable = (dateStr: string): string => {
    const date = new Date(dateStr + 'T00:00:00'); // Add time to avoid timezone issues
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  // Get sorted dates (should only be one date in the response)
  const dates = Object.keys(data.near_earth_objects).sort();

  return (
    <div className={styles.container}>
      <div className={styles.controls}>
        <div className={styles.datePickerWrapper}>
          <label htmlFor="date-picker" className={styles.dateLabel}>
            Select Date:
          </label>
          <input
            id="date-picker"
            type="date"
            value={selectedDate}
            onChange={handleDateChange}
            className={styles.datePicker}
          />
        </div>

        <div className={styles.summary}>
          <div className={styles.summaryCount}>
            {data.element_count}
          </div>
          <div className={styles.summaryText}>
            asteroid{data.element_count !== 1 ? 's' : ''} approaching Earth on
            <div className={styles.summaryDate}>
              {formatDateReadable(selectedDate)}
            </div>
          </div>
        </div>
      </div>

      {dates.map((date) => {
        const asteroids = data.near_earth_objects[date];
        return (
          <div key={date} className={styles.dateSection}>
            <h2 className={styles.dateHeader}>{date}</h2>
            <p className={styles.asteroidCount}>
              {asteroids.length} asteroid{asteroids.length !== 1 ? 's' : ''}
            </p>

            <div className={styles.asteroidList}>
              {asteroids.map((asteroid) => {
                const approach = asteroid.close_approach_data[0];
                return (
                  <div key={asteroid.id} className={styles.asteroidCard}>
                    <div className={styles.asteroidHeader}>
                      <h3 className={styles.asteroidName}>{asteroid.name}</h3>
                      {asteroid.is_potentially_hazardous_asteroid && (
                        <span className={styles.hazardBadge}>
                          Potentially Hazardous
                        </span>
                      )}
                    </div>

                    <div className={styles.asteroidDetails}>
                      <div className={styles.detailRow}>
                        <strong>Estimated Diameter:</strong>{' '}
                        {asteroid.estimated_diameter.kilometers.estimated_diameter_min.toFixed(
                          3
                        )}{' '}
                        -{' '}
                        {asteroid.estimated_diameter.kilometers.estimated_diameter_max.toFixed(
                          3
                        )}{' '}
                        km
                      </div>

                      <div className={styles.detailRow}>
                        <strong>Absolute Magnitude:</strong>{' '}
                        {asteroid.absolute_magnitude_h.toFixed(2)}
                      </div>

                      {approach && (
                        <>
                          <div className={styles.detailRow}>
                            <strong>Close Approach:</strong>{' '}
                            {approach.close_approach_date_full}
                          </div>

                          <div className={styles.detailRow}>
                            <strong>Velocity:</strong>{' '}
                            {parseFloat(
                              approach.relative_velocity.kilometers_per_hour
                            ).toLocaleString()}{' '}
                            km/h
                          </div>

                          <div className={styles.detailRow}>
                            <strong>Miss Distance:</strong>{' '}
                            {parseFloat(
                              approach.miss_distance.kilometers
                            ).toLocaleString()}{' '}
                            km ({parseFloat(approach.miss_distance.lunar).toFixed(2)}{' '}
                            lunar distances)
                          </div>
                        </>
                      )}
                    </div>

                    <a
                      href={`/asteroid/${asteroid.id}`}
                      className={styles.detailsLink}
                    >
                      View Details →
                    </a>
                  </div>
                );
              })}
            </div>
          </div>
        );
      })}
    </div>
  );
}
