import type { ProductSummary } from '../../hooks/types';
import { getAvailabilityBadgeLabel } from '../../utils/availabilityStatus';
import { AddToCartButton } from './AddToCartButton';
import { AvailabilityBadge } from './AvailabilityBadge';
import { MoreDetailsOverlay } from './MoreDetailsOverlay';
import { ProductImage } from './ProductImage';
import { ProductPrice } from './ProductPrice';
import { ProductTitle } from './ProductTitle';
import { ReviewStars } from './ReviewStars';

interface ProductCardProps {
  product: ProductSummary;
  isDetailsOpen: boolean;
  onOpenDetails: (productId: number) => void;
  priority?: boolean;
}

export function ProductCard({
  product,
  isDetailsOpen,
  onOpenDetails,
  priority = false,
}: ProductCardProps) {
  const availabilityLabel = getAvailabilityBadgeLabel(product.availabilityStatus);
  const accessibleName = availabilityLabel
    ? `${product.title}, ${availabilityLabel}`
    : product.title;

  return (
    <article
      aria-label={accessibleName}
      className="flex h-full flex-col overflow-hidden rounded border border-border bg-elevated"
    >
      <div className="group relative">
        <ProductImage src={product.thumbnail} alt={product.title} priority={priority} />
        <AvailabilityBadge availabilityStatus={product.availabilityStatus} />
        <MoreDetailsOverlay
          productId={product.id}
          isOpen={isDetailsOpen}
          onOpenDetails={onOpenDetails}
        />
      </div>
      <div className="flex flex-1 flex-col gap-2 p-3">
        <ProductTitle
          title={product.title}
          {...(product.brand !== undefined ? { brand: product.brand } : {})}
        />
        <div className="mt-auto flex flex-col gap-2">
          <ProductPrice
            price={product.price}
            discountPercentage={product.discountPercentage}
          />
          <ReviewStars
            rating={product.rating}
            reviewCount={product.reviewCount}
          />
          <AddToCartButton
            sku={product.sku}
            title={product.title}
            price={product.price}
            discountPercentage={product.discountPercentage}
            thumbnail={product.thumbnail}
            availabilityStatus={product.availabilityStatus}
            productId={product.id}
            onOpenDetails={onOpenDetails}
          />
        </div>
      </div>
    </article>
  );
}
