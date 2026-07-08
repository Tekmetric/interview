import type { Product } from '../../hooks/types';
import { AddToCartButton } from './AddToCartButton';
import { MoreDetailsOverlay } from './MoreDetailsOverlay';
import { ProductImage } from './ProductImage';
import { ProductPrice } from './ProductPrice';
import { ProductTitle } from './ProductTitle';
import { ReviewStars } from './ReviewStars';

interface ProductCardProps {
  product: Product;
}

export function ProductCard({ product }: ProductCardProps) {
  return (
    <article className="flex h-full flex-col overflow-hidden rounded border border-neutral-200">
      <div className="group relative">
        <ProductImage src={product.thumbnail} alt={product.title} />
        <MoreDetailsOverlay sku={product.sku} />
      </div>
      <div className="flex flex-1 flex-col gap-2 p-3">
        <ProductTitle title={product.title} brand={product.brand} />
        <ProductPrice
          price={product.price}
          discountPercentage={product.discountPercentage}
        />
        <ReviewStars
          rating={product.rating}
          reviewCount={product.reviews.length}
        />
        <AddToCartButton sku={product.sku} />
      </div>
    </article>
  );
}
