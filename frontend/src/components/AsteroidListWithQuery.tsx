import QueryProvider from './QueryProvider';
import AsteroidList from './AsteroidList';

export default function AsteroidListWithQuery() {
  return (
    <QueryProvider>
      <AsteroidList />
    </QueryProvider>
  );
}
