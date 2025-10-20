import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { DayPicker, type DateRange } from 'react-day-picker';
import 'react-day-picker/style.css';
import styles from './AsteroidFeed.module.css';
import { NeoWsFeedResponseSchema } from '../schemas/nasa';

export default function AsteroidFeed() {
  const [selectedRange, setSelectedRange] = useState<DateRange | undefined>({
    from: new Date(),
    to: undefined,
  });

  // Convert Date to YYYY-MM-DD string
  const dateToString = (date: Date): string => {
    return date.toISOString().split('T')[0];
  };

  // Calculate disabled dates (beyond 7 days from selected start)
  const getDisabledDates = () => {
    if (!selectedRange?.from) return undefined;

    const maxDate = new Date(selectedRange.from);
    maxDate.setDate(maxDate.getDate() + 6); // 7 days total

    return [
      { after: maxDate }
    ];
  };

  // Calculate date parameters
  const startDate = selectedRange?.from ? dateToString(selectedRange.from) : '';
  const endDate = selectedRange?.to ? dateToString(selectedRange.to) : startDate;

  // Fetch data with React Query
  const { data, isLoading, error } = useQuery({
    queryKey: ['asteroids', 'feed', startDate, endDate],
    queryFn: async () => {
      const response = await fetch(
        `/api/neows-feed?start_date=${startDate}&end_date=${endDate}`
      );

      if (!response.ok) {
        throw new Error('Failed to fetch asteroid data');
      }

      // Parse and validate response with Zod
      const responseData = NeoWsFeedResponseSchema.parse(await response.json());

      return responseData;
    },
    enabled: !!selectedRange?.from,
  });

  const handleRangeSelect = (range: DateRange | undefined) => {
    if (range?.from) {
      // If selecting a range beyond 7 days, cap it
      if (range.to) {
        const daysDiff = Math.ceil(
          (range.to.getTime() - range.from.getTime()) / (1000 * 60 * 60 * 24)
        );

        if (daysDiff > 6) {
          // Cap at 7 days
          const maxTo = new Date(range.from);
          maxTo.setDate(maxTo.getDate() + 6);
          setSelectedRange({ from: range.from, to: maxTo });
          return;
        }
      }

      setSelectedRange(range);
    }
  };

  if (isLoading && !data) {
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

  // Calculate display text
  const getDisplayText = () => {
    if (!selectedRange?.from) return '';

    if (!selectedRange.to || selectedRange.from.getTime() === selectedRange.to.getTime()) {
      return `in ${dateToString(selectedRange.from)}`;
    }

    const daysDiff = Math.ceil(
      (selectedRange.to.getTime() - selectedRange.from.getTime()) / (1000 * 60 * 60 * 24)
    ) + 1;

    return `between ${dateToString(selectedRange.from)} and ${dateToString(selectedRange.to)}`;
  };

  return (
    <div className={styles.container}>
      <div className={styles.controls}>
        <div className={styles.calendarWrapper}>
          <DayPicker
            mode="range"
            selected={selectedRange}
            onSelect={handleRangeSelect}
            disabled={getDisabledDates()}
            className={styles.calendar}
            max={7}
          />
        </div>
        <div className={styles.infoPanel}>
          <p className={styles.dateInfo}>
            Select a date range (up to 7 days) to view asteroids approaching Earth
          </p>
          {data && (
            <div className={styles.summary}>
              <p>
                <strong>{data.element_count}</strong> asteroid
                {data.element_count !== 1 ? 's' : ''} approaching Earth {getDisplayText()}
              </p>
            </div>
          )}
        </div>
      </div>

      {isLoading && (
        <div className={styles.loadingOverlay}>
          <p>Updating...</p>
        </div>
      )}

      {error && (
        <div className={styles.error}>
          <p>Error: {error.message}</p>
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
