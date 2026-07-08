import { Button } from '../button/Button';
import './moreDetailsOverlay.css';

interface MoreDetailsOverlayProps {
  sku: string;
}

function MagnifyingGlassIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="h-5 w-5">
      <circle cx="11" cy="11" r="7" fill="none" stroke="currentColor" strokeWidth="2" />
      <line x1="16.5" y1="16.5" x2="21" y2="21" stroke="currentColor" strokeWidth="2" />
    </svg>
  );
}

export function MoreDetailsOverlay({ sku }: MoreDetailsOverlayProps) {
  const handleClick = () => {
    console.log('More details clicked', { sku });
  };

  return (
    <>
      <div className="details-overlay">
        <Button variant="secondary" aria-label="View Details" onClick={handleClick}>
          View Details
        </Button>
      </div>
      <button
        type="button"
        className="details-corner"
        aria-label="View Details"
        onClick={handleClick}
      >
        <MagnifyingGlassIcon />
      </button>
    </>
  );
}
