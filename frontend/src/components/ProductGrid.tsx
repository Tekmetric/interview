import type { Product } from '../hooks/types';
import { ProductCard } from './product_card/ProductCard';
import './product_card/productCard.css';

interface ProductGridProps {
  products: Product[];
}

export function ProductGrid({ products }: ProductGridProps) {
  return (
    <ul className="product-grid">
      {products.map((product) => (
        <li key={product.id} className="product-grid__item">
          <ProductCard product={product} />
        </li>
      ))}
    </ul>
  );
}
