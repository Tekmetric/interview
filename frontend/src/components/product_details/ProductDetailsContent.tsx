import type { ProductDetail } from '../../hooks/types';
import { CollapsibleSection } from './CollapsibleSection';
import { ProductDetailsAction } from './ProductDetailsAction';
import { ProductDetailsHero } from './ProductDetailsHero';
import { ProductReviewPager } from './ProductReviewPager';
import './productDetails.css';

interface ProductDetailsContentProps {
  product: ProductDetail;
}

export function ProductDetailsContent({ product }: ProductDetailsContentProps) {
  const { width, height, depth } = product.dimensions;
  const inStock = product.stock > 0;

  return (
    <div className="product-details-content">
      <ProductDetailsHero product={product} />

      <div className="product-details-sections">
        {product.description.trim() ? (
          <p className="product-details-description">{product.description}</p>
        ) : null}

        <CollapsibleSection title="Product Specifications">
          <dl className="product-details-specs">
            <div>
              <dt>Weight</dt>
              <dd>{product.weight}</dd>
            </div>
            <div>
              <dt>Dimensions</dt>
              <dd>
                {width} × {height} × {depth}
              </dd>
            </div>
          </dl>
        </CollapsibleSection>

        <CollapsibleSection title="Shipping and Inventory">
          <dl className="product-details-specs">
            <div>
              <dt>Shipping information</dt>
              <dd>{product.shippingInformation}</dd>
            </div>
            <div>
              <dt>Availability status</dt>
              <dd>{product.availabilityStatus}</dd>
            </div>
            <div>
              <dt>Stock</dt>
              <dd>{product.stock}</dd>
            </div>
            <div>
              <dt>Return policy</dt>
              <dd>{product.returnPolicy}</dd>
            </div>
          </dl>
        </CollapsibleSection>

        <CollapsibleSection title="Reviews">
          <ProductReviewPager key={product.id} reviews={product.reviews} />
        </CollapsibleSection>
      </div>

      <ProductDetailsAction
        sku={product.sku}
        title={product.title}
        price={product.price}
        discountPercentage={product.discountPercentage}
        thumbnail={product.thumbnail}
        inStock={inStock}
      />
    </div>
  );
}
