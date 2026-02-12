import { useEffect, useState } from "react";
import { fetchProducts } from "../api/productsApi";
import type { Product } from "../types/Product";

export const useProducts = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [pages, setPages] = useState(1);

  const loadProducts = async (pageNumber: number = 1) => {
    setLoading(true);
    try {
      const data = await fetchProducts(pageNumber);
      setProducts(data.products);
      setPages(data.pages);
      setPage(data.page);
      setError(null);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProducts(page);
  }, [page]);

  return { products, loading, error, page, pages, setPage };
};
