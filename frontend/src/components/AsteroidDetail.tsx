import { useQuery } from '@tanstack/react-query';
import styles from './AsteroidDetail.module.css';
import type { AsteroidDetail as AsteroidData } from '../schemas/nasa';

interface AsteroidDetailProps {
  asteroidId: string;
}

export default function AsteroidDetail({ asteroidId }: AsteroidDetailProps) {
  // Fetch data with React Query
  const { data: asteroid, isLoading, error } = useQuery<AsteroidData, Error>({
    queryKey: ['asteroid', asteroidId],
    queryFn: async () => {
      const response = await fetch(`/api/asteroid/${asteroidId}`);

      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('Asteroid not found');
        }
        throw new Error('Failed to fetch asteroid data');
      }

      const data = await response.json();

      if (data.error) {
        throw new Error(data.error);
      }

      return data;
    },
  });

  if (isLoading) {
    return (
      <div className={styles.loading}>
        <p>Loading asteroid details...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className={styles.error}>
        <p>Error: {error.message}</p>
        <a href="/browse" className={styles.backLink}>
          ← Back to List
        </a>
      </div>
    );
  }

  if (!asteroid) {
    return null;
  }

  return (
    <div className={styles.container}>
      <a href="/browse" className={styles.backLink}>
        ← Back to List
      </a>

      <div className={styles.header}>
        <h1 className={styles.title}>{asteroid.designation}</h1>
        <div className={styles.badges}>
          {asteroid.is_potentially_hazardous_asteroid && (
            <span className={styles.hazardBadge}>Potentially Hazardous</span>
          )}
          {asteroid.is_sentry_object && (
            <span className={styles.sentryBadge}>Sentry Object</span>
          )}
        </div>
      </div>

      <div className={styles.sections}>
        {/* Basic Information */}
        <section className={styles.section}>
          <h2>Basic Information</h2>
          <div className={styles.infoGrid}>
            <div className={styles.infoItem}>
              <strong>NEO Reference ID:</strong>
              <span>{asteroid.neo_reference_id}</span>
            </div>
            <div className={styles.infoItem}>
              <strong>Designation:</strong>
              <span>{asteroid.designation}</span>
            </div>
            <div className={styles.infoItem}>
              <strong>Absolute Magnitude:</strong>
              <span>{asteroid.absolute_magnitude_h.toFixed(2)}</span>
            </div>
          </div>
        </section>

        {/* Estimated Diameter */}
        <section className={styles.section}>
          <h2>Estimated Diameter</h2>
          <div className={styles.infoGrid}>
            <div className={styles.infoItem}>
              <strong>Kilometers:</strong>
              <span>
                {asteroid.estimated_diameter.kilometers.estimated_diameter_min.toFixed(
                  3
                )}{' '}
                -{' '}
                {asteroid.estimated_diameter.kilometers.estimated_diameter_max.toFixed(
                  3
                )}{' '}
                km
              </span>
            </div>
            <div className={styles.infoItem}>
              <strong>Meters:</strong>
              <span>
                {asteroid.estimated_diameter.meters.estimated_diameter_min.toFixed(
                  1
                )}{' '}
                -{' '}
                {asteroid.estimated_diameter.meters.estimated_diameter_max.toFixed(
                  1
                )}{' '}
                m
              </span>
            </div>
            <div className={styles.infoItem}>
              <strong>Miles:</strong>
              <span>
                {asteroid.estimated_diameter.miles.estimated_diameter_min.toFixed(
                  3
                )}{' '}
                -{' '}
                {asteroid.estimated_diameter.miles.estimated_diameter_max.toFixed(
                  3
                )}{' '}
                mi
              </span>
            </div>
            <div className={styles.infoItem}>
              <strong>Feet:</strong>
              <span>
                {asteroid.estimated_diameter.feet.estimated_diameter_min.toFixed(
                  0
                )}{' '}
                -{' '}
                {asteroid.estimated_diameter.feet.estimated_diameter_max.toFixed(
                  0
                )}{' '}
                ft
              </span>
            </div>
          </div>
        </section>

        {/* Orbital Data */}
        <section className={styles.section}>
          <h2>Orbital Data</h2>
          <div className={styles.infoGrid}>
            <div className={styles.infoItem}>
              <strong>Orbit ID:</strong>
              <span>{asteroid.orbital_data.orbit_id}</span>
            </div>
            <div className={styles.infoItem}>
              <strong>Orbit Class:</strong>
              <span>{asteroid.orbital_data.orbit_class.orbit_class_type}</span>
            </div>
            <div className={styles.infoItem}>
              <strong>Orbital Period:</strong>
              <span>{parseFloat(asteroid.orbital_data.orbital_period).toFixed(2)} days</span>
            </div>
            <div className={styles.infoItem}>
              <strong>Eccentricity:</strong>
              <span>{parseFloat(asteroid.orbital_data.eccentricity).toFixed(4)}</span>
            </div>
            <div className={styles.infoItem}>
              <strong>Semi-Major Axis:</strong>
              <span>{parseFloat(asteroid.orbital_data.semi_major_axis).toFixed(4)} AU</span>
            </div>
            <div className={styles.infoItem}>
              <strong>Inclination:</strong>
              <span>{parseFloat(asteroid.orbital_data.inclination).toFixed(2)}°</span>
            </div>
            <div className={styles.infoItem}>
              <strong>Perihelion Distance:</strong>
              <span>{parseFloat(asteroid.orbital_data.perihelion_distance).toFixed(4)} AU</span>
            </div>
            <div className={styles.infoItem}>
              <strong>Aphelion Distance:</strong>
              <span>{parseFloat(asteroid.orbital_data.aphelion_distance).toFixed(4)} AU</span>
            </div>
            <div className={styles.infoItem}>
              <strong>First Observation:</strong>
              <span>{asteroid.orbital_data.first_observation_date}</span>
            </div>
            <div className={styles.infoItem}>
              <strong>Last Observation:</strong>
              <span>{asteroid.orbital_data.last_observation_date}</span>
            </div>
            <div className={styles.infoItem}>
              <strong>Observations Used:</strong>
              <span>{asteroid.orbital_data.observations_used}</span>
            </div>
            <div className={styles.infoItem}>
              <strong>Data Arc:</strong>
              <span>{asteroid.orbital_data.data_arc_in_days} days</span>
            </div>
          </div>
        </section>

        {/* Close Approach Data */}
        <section className={styles.section}>
          <h2>Close Approach Data ({asteroid.close_approach_data.length} approaches)</h2>
          <div className={styles.approachesList}>
            {asteroid.close_approach_data.map((approach, index) => (
              <div key={index} className={styles.approachCard}>
                <h3 className={styles.approachDate}>
                  {approach.close_approach_date_full}
                </h3>
                <div className={styles.infoGrid}>
                  <div className={styles.infoItem}>
                    <strong>Orbiting Body:</strong>
                    <span>{approach.orbiting_body}</span>
                  </div>
                  <div className={styles.infoItem}>
                    <strong>Velocity:</strong>
                    <span>
                      {parseFloat(approach.relative_velocity.kilometers_per_hour).toLocaleString()}{' '}
                      km/h
                    </span>
                  </div>
                  <div className={styles.infoItem}>
                    <strong>Miss Distance:</strong>
                    <span>
                      {parseFloat(approach.miss_distance.kilometers).toLocaleString()} km
                    </span>
                  </div>
                  <div className={styles.infoItem}>
                    <strong>Lunar Distance:</strong>
                    <span>{parseFloat(approach.miss_distance.lunar).toFixed(2)} LD</span>
                  </div>
                  <div className={styles.infoItem}>
                    <strong>Astronomical Units:</strong>
                    <span>{parseFloat(approach.miss_distance.astronomical).toFixed(4)} AU</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>

      <div className={styles.externalLink}>
        <a
          href={asteroid.nasa_jpl_url}
          target="_blank"
          rel="noopener noreferrer"
          className={styles.jplLink}
        >
          View Full Details on NASA JPL →
        </a>
      </div>
    </div>
  );
}
