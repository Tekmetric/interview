import { useState, useEffect } from 'react';
import { fetchCardSets } from '../api/fetchCardSets';

/**
 * Used to bootstrap the application.
 * @returns
 */
const useBootstrap = () => {
  const [cardSets, setCardSets] = useState(null);
  const [isBootstrapping, setIsBootstrapping] = useState(true);
  const [bootstrapError, setBootstrapError] = useState(null);

  useEffect(() => {
    const bootstrap = async () => {
      try {
        // I set up a Promise.all here in case
        // I needed more bootstrapping in the future :)
        const [data] = await Promise.all([fetchCardSets()]);
        setCardSets(data);
      } catch (err) {
        setBootstrapError(err);
      } finally {
        setIsBootstrapping(false);
      }
    };

    bootstrap();
  }, []);

  return { cardSets, isBootstrapping, bootstrapError };
};

export { useBootstrap };
