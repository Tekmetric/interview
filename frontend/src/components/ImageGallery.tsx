import { useState, useEffect } from 'react';
import styles from './ImageGallery.module.css';

interface ApodData {
  title: string;
  date: string;
  explanation: string;
  url: string;
  hdurl?: string;
  media_type: string;
}

export default function ImageGallery() {
  const [apodData, setApodData] = useState<ApodData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchApod = async () => {
      try {
        const response = await fetch('/api/apod');

        if (!response.ok) {
          throw new Error('Failed to fetch APOD data');
        }

        const data = await response.json();

        // Check if there was an error from our API
        if (data.error) {
          throw new Error(data.error);
        }

        setApodData(data);
        setLoading(false);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An error occurred');
        setLoading(false);
      }
    };

    fetchApod();
  }, []);

  if (loading) {
    return (
      <div className={styles.loading}>
        <p>Loading today's astronomy picture...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className={styles.error}>
        <p>Error: {error}</p>
      </div>
    );
  }

  if (!apodData) {
    return null;
  }

  return (
    <div className={styles.gallery}>
      <h2 className={styles.title}>{apodData.title}</h2>
      <p className={styles.date}>{apodData.date}</p>

      {apodData.media_type === 'image' ? (
        <img
          src={apodData.url}
          alt={apodData.title}
          className={styles.image}
        />
      ) : (
        <div className={styles.videoPlaceholder}>
          <p>Today's APOD is a video.</p>
          <a
            href={apodData.url}
            target="_blank"
            rel="noopener noreferrer"
          >
            View Video
          </a>
        </div>
      )}

      <p className={styles.explanation}>{apodData.explanation}</p>

      {apodData.hdurl && apodData.media_type === 'image' && (
        <p className={styles.hdLink}>
          <a
            href={apodData.hdurl}
            target="_blank"
            rel="noopener noreferrer"
          >
            View High Resolution Image
          </a>
        </p>
      )}
    </div>
  );
}
