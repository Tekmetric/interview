import type { ProductDetail } from '../../hooks/types';
import { ProductImage } from '../product_card/ProductImage';
import './productDetails.css';

interface ProductImageGalleryProps {
  product: ProductDetail;
}

export function ProductImageGallery({ product }: ProductImageGalleryProps) {
  const heroSrc = product.images[0] ?? product.thumbnail;

  return (
    <div className="product-image-gallery">
      <ProductImage src={heroSrc} alt={product.title} />
    </div>
  );
}
