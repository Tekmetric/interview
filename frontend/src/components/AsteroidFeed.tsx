import { useState, useEffect } from 'react';
import styles from './AsteroidFeed.module.css';

interface CloseApproachData {
  close_approach_date: string;
  close_approach_date_full: string;
  relative_velocity: {
    kilometers_per_hour: string;
  };
  miss_distance: {
    kilometers: string;
    lunar: string;
  };
}

interface Asteroid {
  id: string;
  name: string;
  nasa_jpl_url: string;
  absolute_magnitude_h: number;
  is_potentially_hazardous_asteroid: boolean;
  estimated_diameter: {
    kilometers: {
      estimated_diameter_min: number;
      estimated_diameter_max: number;
    };
  };
  close_approach_data: CloseApproachData[];
}

interface NeoWsFeedResponse {
  element_count: number;
  near_earth_objects: {
    [date: string]: Asteroid[];
  };
  error?: string;
}

export default function AsteroidFeed() {
  const [data, setData] = useState<NeoWsFeedResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [startDate, setStartDate] = useState('');

  // Initialize with today's date
  useEffect(() => {
    const today = new Date().toISOString().split('T')[0];
    setStartDate(today);
  }, []);

  // Fetch data when start date changes
  useEffect(() => {
    if (!startDate) return;

    const fetchAsteroids = async () => {
      setLoading(true);
      setError(null);

      try {
        // Calculate end date (7 days later for max range)
        const start = new Date(startDate);
        const end = new Date(start);
        end.setDate(end.getDate() + 6); // 7 days total (including start)
        const endDate = end.toISOString().split('T')[0];

        const response = await fetch(
          `/api/neows-feed?start_date=${startDate}&end_date=${endDate}`
        );

        if (!response.ok) {
          throw new Error('Failed to fetch asteroid data');
        }

        const responseData = await response.json();

        if (responseData.error) {
          throw new Error(responseData.error);
        }

        setData(responseData);
        setLoading(false);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An error occurred');
        setLoading(false);
      }
    };

    fetchAsteroids();
  }, [startDate]);

  const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setStartDate(e.target.value);
  };

  if (loading && !data) {
    return (
      <div className={styles.loading}>
        <p>Loading asteroids...</p>
      </div>
    );
  }

  // Get sorted dates
  const dates = data?.near_earth_objects
    ? Object.keys(data.near_earth_objects).sort()
    : [];

  return (
    <div className={styles.container}>
      <div className={styles.controls}>
        <label htmlFor="start-date" className={styles.dateLabel}>
          Select Start Date:
        </label>
        <input
          id="start-date"
          type="date"
          value={startDate}
          onChange={handleDateChange}
          className={styles.dateInput}
        />
        <p className={styles.dateInfo}>
          Showing 7-day period from {startDate}
        </p>
      </div>

      {loading && (
        <div className={styles.loadingOverlay}>
          <p>Updating...</p>
        </div>
      )}

      {error && (
        <div className={styles.error}>
          <p>Error: {error}</p>
        </div>
      )}

      {data && (
        <div className={styles.summary}>
          <p>
            <strong>{data.element_count}</strong> asteroid
            {data.element_count !== 1 ? 's' : ''} approaching Earth in this
            period
          </p>
        </div>
      )}

      {dates.map((date) => {
        const asteroids = data!.near_earth_objects[date];
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
                      href={asteroid.nasa_jpl_url}
                      target="_blank"
                      rel="noopener noreferrer"
                      className={styles.detailsLink}
                    >
                      View Details on NASA JPL
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
