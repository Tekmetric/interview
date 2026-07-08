interface MoreDetailsOverlayProps {
  sku: string;
}

function MagnifyingGlassIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="product-card__details-icon">
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
      <div className="product-card__details-overlay">
        <button
          type="button"
          className="product-card__button product-card__button--secondary"
          aria-label="View Details"
          onClick={handleClick}
        >
          View Details
        </button>
      </div>
      <button
        type="button"
        className="product-card__details-corner"
        aria-label="View Details"
        onClick={handleClick}
      >
        <MagnifyingGlassIcon />
      </button>
    </>
  );
}
