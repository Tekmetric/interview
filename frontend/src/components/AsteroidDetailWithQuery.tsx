import QueryProvider from './QueryProvider';
import AsteroidDetail from './AsteroidDetail';

interface AsteroidDetailWithQueryProps {
  asteroidId: string;
}

export default function AsteroidDetailWithQuery({ asteroidId }: AsteroidDetailWithQueryProps) {
  return (
    <QueryProvider>
      <AsteroidDetail asteroidId={asteroidId} />
    </QueryProvider>
  );
}
