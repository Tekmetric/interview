import { Link } from 'react-router-dom';
import { IconImage } from '../components/icons';

export default function NotFoundPage() {
  return (
    <div className="flex flex-col items-center gap-4 py-16 text-center">
      <span className="flex size-14 items-center justify-center rounded-full bg-surface-2 text-muted">
        <IconImage className="size-7" />
      </span>
      <h1 className="text-2xl font-bold">Page not found</h1>
      <p className="text-muted">This gallery doesn't exist.</p>
      <Link to="/" className="text-accent hover:underline">
        Back to search
      </Link>
    </div>
  );
}
