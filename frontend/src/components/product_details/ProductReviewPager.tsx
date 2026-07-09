import { useState } from 'react';
import type { ProductDetailReview } from '../../hooks/types';
import { Button } from '../button/Button';
import '../product_card/reviewStars.css';
import './productDetails.css';

interface ProductReviewPagerProps {
  reviews: ProductDetailReview[];
}

function IntegerStarIcon({ filled }: { filled: boolean }) {
  if (filled) {
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

function IntegerReviewStars({ rating }: { rating: number }) {
  const filledCount = Math.min(5, Math.max(0, Math.round(rating)));
  const label = `${filledCount} out of 5 stars`;

  return (
    <div className="flex items-center gap-[0.1rem]" role="img" aria-label={label}>
      {Array.from({ length: 5 }, (_, index) => (
        <IntegerStarIcon key={index} filled={index < filledCount} />
      ))}
    </div>
  );
}

function formatReviewDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
}

export function ProductReviewPager({ reviews }: ProductReviewPagerProps) {
  const [currentIndex, setCurrentIndex] = useState(0);

  if (reviews.length === 0) {
    return <p className="product-review-pager__empty">No reviews yet.</p>;
  }

  const review = reviews[currentIndex];
  const statusLabel = `Review ${currentIndex + 1} of ${reviews.length}`;

  return (
    <div className="product-review-pager">
      <div className="product-review-pager__header">
        <span className="text-sm text-neutral-600" aria-live="polite">
          {statusLabel}
        </span>
        <div className="product-review-pager__controls">
          <Button
            variant="secondary"
            aria-label="Previous review"
            disabled={currentIndex === 0}
            onClick={() => setCurrentIndex((index) => index - 1)}
          >
            Back
          </Button>
          <Button
            variant="secondary"
            aria-label="Next review"
            disabled={currentIndex === reviews.length - 1}
            onClick={() => setCurrentIndex((index) => index + 1)}
          >
            Forward
          </Button>
        </div>
      </div>

      <article className="product-review-pager__review">
        <IntegerReviewStars rating={review.rating} />
        <p className="product-review-pager__meta m-0">
          <span className="font-medium text-neutral-800">{review.reviewerName}</span>
          {' · '}
          <time dateTime={review.date}>{formatReviewDate(review.date)}</time>
        </p>
        <p className="product-review-pager__comment">{review.comment}</p>
      </article>
    </div>
  );
}
