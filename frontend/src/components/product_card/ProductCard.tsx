import type { ProductSummary } from '../../hooks/types';
import { AddToCartButton } from './AddToCartButton';
import { MoreDetailsOverlay } from './MoreDetailsOverlay';
import { ProductImage } from './ProductImage';
import { ProductPrice } from './ProductPrice';
import { ProductTitle } from './ProductTitle';
import { ReviewStars } from './ReviewStars';

interface ProductCardProps {
  product: ProductSummary;
  isDetailsOpen: boolean;
  onOpenDetails: (productId: number) => void;
}

export function ProductCard({
  product,
  isDetailsOpen,
  onOpenDetails,
}: ProductCardProps) {
  return (
    <article className="flex h-full flex-col overflow-hidden rounded border border-neutral-200">
      <div className="group relative">
        <ProductImage src={product.thumbnail} alt={product.title} />
        <MoreDetailsOverlay
          productId={product.id}
          isOpen={isDetailsOpen}
          onOpenDetails={onOpenDetails}
        />
      </div>
      <div className="flex flex-1 flex-col gap-2 p-3">
        <ProductTitle title={product.title} brand={product.brand} />
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
          />
        </div>
      </div>
    </article>
  );
}
