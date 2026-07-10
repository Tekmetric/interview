import { getStarStates, roundToNearestHalf } from './ratingUtils';
import './reviewStars.css';

interface ReviewStarsProps {
  rating: number;
  reviewCount: number;
}

// alternatively these can be assets served from a cdn or similar

function StarIcon({ state, index }: { state: 'full' | 'half' | 'empty'; index: number }) {
  if (state === 'full') {
    return (
      <svg
        className="review-star review-star--full h-4 w-4"
        viewBox="0 0 24 24"
        aria-hidden="true"
      >
        <path d="M12 2l3.09 6.26L22 9.27l-5 4.87L18.18 22 12 18.56 5.82 22 7 14.14l-5-4.87 6.91-1.01L12 2z" />
      </svg>
    );
  }

  if (state === 'half') {
    const clipId = `half-star-clip-${index}`;
    return (
      <svg
        className="review-star review-star--half h-4 w-4"
        viewBox="0 0 24 24"
        aria-hidden="true"
      >
        <defs>
          <clipPath id={clipId}>
            <rect x="0" y="0" width="12" height="24" />
          </clipPath>
        </defs>
        <path
          className="review-star-outline"
          d="M12 2l3.09 6.26L22 9.27l-5 4.87L18.18 22 12 18.56 5.82 22 7 14.14l-5-4.87 6.91-1.01L12 2z"
        />
        <path
          className="review-star-fill"
          d="M12 2l3.09 6.26L22 9.27l-5 4.87L18.18 22 12 18.56 5.82 22 7 14.14l-5-4.87 6.91-1.01L12 2z"
          clipPath={`url(#${clipId})`}
        />
      </svg>
    );
  }

  return (
    <svg
      className="review-star review-star--empty h-4 w-4"
      viewBox="0 0 24 24"
      aria-hidden="true"
    >
      <path
        className="review-star-outline"
        d="M12 2l3.09 6.26L22 9.27l-5 4.87L18.18 22 12 18.56 5.82 22 7 14.14l-5-4.87 6.91-1.01L12 2z"
      />
    </svg>
  );
}

export function ReviewStars({ rating, reviewCount }: ReviewStarsProps) {
  const roundedRating = roundToNearestHalf(rating);
  const starStates = getStarStates(rating);
  const label = `${roundedRating} out of 5 stars, ${reviewCount} reviews`;

  return (
    <div className="flex items-center gap-[0.35rem]">
      <div className="flex items-center gap-[0.1rem]" role="img" aria-label={label}>
        {starStates.map((state, index) => (
          <StarIcon key={index} state={state} index={index} />
        ))}
      </div>
      <span className="text-[0.85rem] text-text-muted" aria-hidden="true">
        ({reviewCount})
      </span>
    </div>
  );
}
