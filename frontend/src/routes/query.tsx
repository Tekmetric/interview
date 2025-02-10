import { getRepoData } from '@api/github';
import { useQuery } from '@tanstack/react-query';
import { createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/query')({
  component: Query,
  pendingComponent: () => 'Loading...'
});

function Query() {
  const { isPending, error, data, isFetching } = useQuery({
    queryKey: ['repoData'],
    queryFn: getRepoData
  });

  if (isPending) {
    return 'Loading...';
  }

  if (error) {
    return `An error has occurred: ${error.message}`;
  }

  return (
    <div>
      <h1>{ data.full_name }</h1>
      <p>{ data.description }</p>
      <strong>
        👀
        { data.subscribers_count }
      </strong>
      { ' ' }
      <strong>
        ✨
        { data.stargazers_count }
      </strong>
      { ' ' }
      <strong>
        🍴
        { data.forks_count }
      </strong>
      <div>{ isFetching ? 'Updating...' : '' }</div>
    </div>
  );
}
