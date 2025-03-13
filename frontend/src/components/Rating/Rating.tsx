import { Star } from 'lucide-react';

interface RatingProps {
  rating: number;
  reviewsCount: number;
  showRatingValue?: boolean;
}

const Rating = ({ rating, reviewsCount, showRatingValue }: RatingProps) => {
  const clampedRating = Math.max(0, Math.min(5, rating));
  const stars = [1, 2, 3, 4, 5];

  // Calculate partial fills for decimal ratings
  const fullStars = Math.floor(clampedRating);
  const hasPartialStar = clampedRating % 1 !== 0;
  const partialFill = (clampedRating % 1) * 100; // Percentage fill for partial star

  return (
    <div className="flex items-center">
      {stars.map((starValue) => {
        // Determine if this star should be full, partial, or empty
        const isFull = starValue <= fullStars;
        const isPartial =
          !isFull && hasPartialStar && starValue === fullStars + 1;

        return (
          <div key={starValue} className="relative">
            <Star
              className={isFull ? 'text-yellow-400' : 'text-gray-300'}
              fill={isFull ? 'currentColor' : 'none'}
              strokeWidth={1.5}
              size={20}
            />

            {isPartial && (
              <div
                className="absolute top-0 left-0 overflow-hidden"
                style={{ width: `${partialFill}%` }}
              >
                <Star
                  className="text-yellow-400"
                  fill="currentColor"
                  strokeWidth={1.5}
                  size={20}
                />
              </div>
            )}
          </div>
        );
      })}

      {showRatingValue && (
        <span className="ml-2 text-sm text-gray-600">{rating}</span>
      )}
      {reviewsCount > 0 && (
        <span className="ml-2 text-sm text-gray-600">
          ({reviewsCount} reviews)
        </span>
      )}
    </div>
  );
};

export default Rating;
