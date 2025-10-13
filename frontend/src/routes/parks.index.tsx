import { createFileRoute, redirect } from '@tanstack/react-router';

export const Route = createFileRoute('/parks/')({
  loader: () => {
    redirect({
      to: '/',
      throw: true,
    });
  },
});
