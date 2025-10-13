import type { ParksApiError } from '@/types/parks-api-error';
import { Button } from './ui/button';
import { Link } from '@tanstack/react-router';

export default function ErrorDisplay({ error, showLink }: { error: ParksApiError; showLink?: boolean }) {
  return (
    <div className="p-8 flex flex-col gap-4 items-center justify-center text-center">
      <h1 className="text-2xl">
        {error.status} - {error.message}
      </h1>
      {showLink && (
        <Button asChild>
          <Link to="/">Return to home</Link>
        </Button>
      )}
    </div>
  );
}
