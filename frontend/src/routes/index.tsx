import Welcome from '@components/Welcome';
import { createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/')({
  component: Index,
  pendingComponent: () => 'Loading...'
});

function Index() {
  return <Welcome />;
}
