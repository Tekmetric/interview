import type { ProductSummary } from '../../hooks/types';
import { ProductCard } from '../product_card/ProductCard';

interface ProductGridProps {
  products: ProductSummary[];
  selectedProductId: number | null;
  onOpenDetails: (productId: number) => void;
}

export function ProductGrid({
  products,
  selectedProductId,
  onOpenDetails,
}: ProductGridProps) {
  return (
    <ul className="grid grid-cols-2 gap-4 list-none m-0 p-0 sm:grid-cols-3 lg:grid-cols-4">
      {products.map((product) => (
        <li key={product.id} className="min-w-0">
          <ProductCard
            product={product}
            isDetailsOpen={selectedProductId === product.id}
            onOpenDetails={onOpenDetails}
          />
        </li>
      ))}
    </ul>
  );
}
