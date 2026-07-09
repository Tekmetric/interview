import type { ProductDetail } from '../../hooks/types';
import { ProductPrice } from '../product_card/ProductPrice';
import { ProductTitle } from '../product_card/ProductTitle';
import { ReviewStars } from '../product_card/ReviewStars';
import { ProductImageGallery } from './ProductImageGallery';
import './productDetails.css';

interface ProductDetailsHeroProps {
  product: ProductDetail;
}

export function ProductDetailsHero({ product }: ProductDetailsHeroProps) {
  const inStock = product.stock > 0;

  return (
    <div className="product-details-hero">
      <div className="product-details-hero__gallery">
        <ProductImageGallery product={product} />
      </div>
      <div className="product-details-hero__info">
        <ProductTitle title={product.title} brand={product.brand} />
        <ReviewStars
          rating={product.rating}
          reviewCount={product.reviews.length}
        />
        <ProductPrice
          price={product.price}
          discountPercentage={product.discountPercentage}
        />
        <p className="m-0 text-sm font-medium text-neutral-700">
          {inStock ? 'In stock' : 'Out of stock'}
        </p>
      </div>
    </div>
  );
}
