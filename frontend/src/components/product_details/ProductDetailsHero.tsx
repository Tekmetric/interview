import type { ProductDetail } from '../../types/product';
import { isProductInStock } from '../../utils/availabilityStatus';
import { ProductPrice } from '../product_card/ProductPrice';
import { ProductTitle } from '../product_card/ProductTitle';
import { ReviewStars } from '../product_card/ReviewStars';
import { ProductImageGallery } from './ProductImageGallery';
import './productDetails.css';

interface ProductDetailsHeroProps {
  product: ProductDetail;
}

export function ProductDetailsHero({ product }: ProductDetailsHeroProps) {
  const inStock = isProductInStock(product);

  return (
    <div className="product-details-hero">
      <div className="product-details-hero__gallery">
        <ProductImageGallery product={product} />
      </div>
      <div className="product-details-hero__info">
        <ProductTitle
          as="h3"
          title={product.title}
          {...(product.brand !== undefined ? { brand: product.brand } : {})}
        />
        <ReviewStars
          rating={product.rating}
          reviewCount={product.reviews.length}
        />
        <ProductPrice
          price={product.price}
          discountPercentage={product.discountPercentage}
        />
        <p className="m-0 text-sm font-medium text-text-secondary">
          {inStock ? 'In stock' : 'Out of stock'}
        </p>
      </div>
    </div>
  );
}
