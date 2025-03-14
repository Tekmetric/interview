import { useQuery } from '@tanstack/react-query';
import { getProduct } from '../api/product.ts';

const useProduct = (id?: string) => {
  const {
    data: product,
    isLoading,
    error,
  } = useQuery({
    queryKey: ['product', id],
    queryFn: () => getProduct(id!),
    staleTime: 1000 * 60 * 5, // 5 minutes
    enabled: !!id,
    retry: 1,
  });

  return { product, isLoading, error };
};

export default useProduct;
