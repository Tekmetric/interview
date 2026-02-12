import React from "react";
import { useProductStore } from "../store/useProductStore";
import ProductCard from "./ProductCard";
import type { Product } from "../types/Product";  

export default React.memo(function ProductsList() {
  const products = useProductStore((s) => s.products); // ✅ only products
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      {products.map((product: Product) => (
        <ProductCard key={product.id} product={product} />
      ))}
    </div>
  );
});
