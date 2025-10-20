import { useState } from 'react';
import { navigate } from 'astro:transitions/client';
import { DayPicker, type DateRange } from 'react-day-picker';
import 'react-day-picker/style.css';
import styles from './AsteroidFeed.module.css';
import type { NeoWsFeedResponse } from '../schemas/nasa';

interface AsteroidFeedProps {
  data: NeoWsFeedResponse;
  initialStartDate: string;
  initialEndDate: string;
}

export default function AsteroidFeed({ data, initialStartDate, initialEndDate }: AsteroidFeedProps) {
  // Parse initial dates from props
  const parseInitialDates = (): DateRange => {
    const from = new Date(initialStartDate);
    const to = initialStartDate === initialEndDate ? undefined : new Date(initialEndDate);
    return { from, to };
  };

  const [selectedRange, setSelectedRange] = useState<DateRange | undefined>(parseInitialDates());

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

  const handleRangeSelect = (range: DateRange | undefined) => {
    if (range?.from) {
      let finalRange = range;

      // If selecting a range beyond 7 days, cap it
      if (range.to) {
        const daysDiff = Math.ceil(
          (range.to.getTime() - range.from.getTime()) / (1000 * 60 * 60 * 24)
        );

        if (daysDiff > 6) {
          // Cap at 7 days
          const maxTo = new Date(range.from);
          maxTo.setDate(maxTo.getDate() + 6);
          finalRange = { from: range.from, to: maxTo };
        }
      }

      // Update local state for immediate UI feedback
      setSelectedRange(finalRange);

      // Navigate to new URL to fetch new data
      const startDate = dateToString(finalRange.from);
      const endDate = finalRange.to ? dateToString(finalRange.to) : startDate;
      navigate(`/?start_date=${startDate}&end_date=${endDate}`);
    }
  };

  // Get sorted dates
  const dates = Object.keys(data.near_earth_objects).sort();

  // Calculate display text
  const getDisplayText = () => {
    if (!selectedRange?.from) return '';

    if (!selectedRange.to || selectedRange.from.getTime() === selectedRange.to.getTime()) {
      return `in ${dateToString(selectedRange.from)}`;
    }

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
