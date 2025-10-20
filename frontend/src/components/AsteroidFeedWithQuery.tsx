import QueryProvider from './QueryProvider';
import AsteroidFeed from './AsteroidFeed';

export default function AsteroidFeedWithQuery() {
  return (
    <QueryProvider>
      <AsteroidFeed />
    </QueryProvider>
  );
}
