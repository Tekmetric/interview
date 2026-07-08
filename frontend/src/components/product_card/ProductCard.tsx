import type { Product } from '../../hooks/types';
import { AddToCartButton } from './AddToCartButton';
import { MoreDetailsOverlay } from './MoreDetailsOverlay';
import { ProductImage } from './ProductImage';
import { ProductPrice } from './ProductPrice';
import { ProductTitle } from './ProductTitle';
import { ReviewStars } from './ReviewStars';
import './productCard.css';

interface ProductCardProps {
  product: Product;
}

export function ProductCard({ product }: ProductCardProps) {
  return (
    <article className="product-card">
      <div className="product-card__media">
        <ProductImage src={product.thumbnail} alt={product.title} />
      </div>
      <div className="product-card__body">
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
      <MoreDetailsOverlay sku={product.sku} />
    </article>
  );
}
